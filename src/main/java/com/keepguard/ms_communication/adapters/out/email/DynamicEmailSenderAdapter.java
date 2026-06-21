package com.keepguard.ms_communication.adapters.out.email;

import com.keepguard.ms_communication.adapters.out.email.dto.EmailSendRequestDTO;
import com.keepguard.ms_communication.adapters.out.email.dto.EmailSendResponseDTO;
import com.keepguard.ms_communication.application.port.out.email.DynamicEmailSenderPort;
import com.keepguard.ms_communication.application.port.out.metrics.MetricsPort;
import com.keepguard.ms_communication.application.service.exception.EmailSendException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class DynamicEmailSenderAdapter implements DynamicEmailSenderPort {

    private final MetricsPort metricsPort;
    
    @Value("${http.client.timeout.connect:10000}")
    private int connectTimeout;
    
    @Value("${http.client.timeout.read:30000}")
    private int readTimeout;
    
    // Tags padrão para métricas
    private static final Map<String, String> EMAIL_TAGS = Map.of(
            "provider", "dynamic-email-sender",
            "service", "ms-communication"
    );

    @Override
    public EmailSendResponseDTO sendEmail(String url, EmailSendRequestDTO request) {
        log.info("Iniciando envio de email dinâmico para: {} via URL: {}", request.getTo(), url);
        
        var sample = metricsPort.startSample();
        
        try {
            RestTemplate restTemplate = createRestTemplate();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set(HttpHeaders.USER_AGENT, "ms-communication/1.0.45-SNAPSHOT");
            headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
            
            HttpEntity<EmailSendRequestDTO> entity = new HttpEntity<>(request, headers);
            
            ResponseEntity<EmailSendResponseDTO> response = restTemplate.exchange(
                    url + "/send/mail",
                    HttpMethod.POST,
                    entity,
                    EmailSendResponseDTO.class
            );
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                // Registra sucesso
                metricsPort.recordSuccess("email.send", "email.send.duration", "ms-communication", sample);
                metricsPort.incrementCounter("email.send.success", EMAIL_TAGS);
                
                log.info("Email enviado com sucesso via URL: {}. MessageId: {}", 
                        url, response.getBody().getMessageId());
                return response.getBody();
            } else {
                // Registra erro
                metricsPort.recordError("email.send", "email.send.duration", "ms-communication", sample);
                metricsPort.incrementCounter("email.send.failure", EMAIL_TAGS);
                
                log.error("Falha no envio de email via URL: {}. Status: {}", 
                        url, response.getStatusCode());
                throw new EmailSendException("Falha no envio de email - status: " + response.getStatusCode());
            }
            
        } catch (org.springframework.web.client.HttpClientErrorException | org.springframework.web.client.HttpServerErrorException e) {
            // Registra erro
            metricsPort.recordError("email.send", "email.send.duration", "ms-communication", sample);
            metricsPort.incrementCounter("email.send.failure", EMAIL_TAGS);
            
            log.error("Erro HTTP no envio de email via URL: {}. Status: {}, Response: {}", 
                    url, e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new EmailSendException("Erro HTTP no envio de email: " + e.getMessage());
            
        } catch (org.springframework.web.client.ResourceAccessException e) {
            // Registra erro
            metricsPort.recordError("email.send", "email.send.duration", "ms-communication", sample);
            metricsPort.incrementCounter("email.send.failure", EMAIL_TAGS);
            
            // Verifica se é erro de hostname não encontrado (ambiente local vs Docker)
            String errorMessage;
            if (e.getCause() instanceof java.net.UnknownHostException) {
                String hostname = extractHostnameFromUrl(url);
                errorMessage = String.format(
                    "Serviço de email não encontrado: '%s'. " +
                    "Verifique se o serviço está rodando e se a URL está correta para o ambiente atual. " +
                    "Para ambiente local, use 'localhost'; para Docker, use o nome do container.",
                    hostname
                );
            } else {
                errorMessage = String.format(
                    "Erro de conectividade com o serviço de email em '%s': %s. " +
                    "Verifique se o serviço está rodando e acessível.",
                    url, e.getMessage()
                );
            }
            
            log.error("Erro de conectividade no envio de email via URL: {}. Causa: {}", url, e.getMessage(), e);
            throw new EmailSendException(errorMessage, e);
            
        } catch (Exception e) {
            // Registra erro
            metricsPort.recordError("email.send", "email.send.duration", "ms-communication", sample);
            metricsPort.incrementCounter("email.send.failure", EMAIL_TAGS);
            
            log.error("Erro inesperado no envio de email via URL: {}", url, e);
            throw new EmailSendException("Erro inesperado no envio de email: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean testConnection(String url) {
        log.info("Testando conexão com serviço de email via URL: {}", url);
        
        try {
            RestTemplate restTemplate = createRestTemplate();
            
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<String> response = restTemplate.exchange(
                    url + "/health",
                    HttpMethod.GET,
                    entity,
                    String.class
            );
            
            boolean isHealthy = response.getStatusCode().is2xxSuccessful() && 
                              response.getBody() != null && 
                              response.getBody().contains("UP");
            log.info("Teste de conexão via URL: {} - Status: {}", url, isHealthy ? "SUCCESS" : "FAILED");
            return isHealthy;
            
        } catch (Exception e) {
            log.warn("Falha no teste de conexão via URL: {}. Erro: {}", url, e.getMessage());
            return false;
        }
    }
    
    /**
     * Cria RestTemplate configurado para requisições HTTP
     */
    private RestTemplate createRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        // Configurações de timeout podem ser adicionadas aqui se necessário
        return restTemplate;
    }
    
    /**
     * Extrai o hostname de uma URL para exibição em mensagens de erro
     */
    private String extractHostnameFromUrl(String url) {
        try {
            java.net.URI uri = java.net.URI.create(url);
            return uri.getHost() != null ? uri.getHost() : "hostname-desconhecido";
        } catch (Exception e) {
            return "url-inválida";
        }
    }
}
