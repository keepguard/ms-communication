package com.keepguard.ms_communication.adapters.out.email;

import com.keepguard.ms_communication.adapters.out.email.dto.EmailSendRequestDTO;
import com.keepguard.ms_communication.adapters.out.email.dto.EmailSendResponseDTO;
import com.keepguard.ms_communication.adapters.out.feign.DynamicEmailSenderClient;
import com.keepguard.ms_communication.application.port.out.email.DynamicEmailSenderPort;
import com.keepguard.ms_communication.application.port.out.metrics.MetricsPort;
import com.keepguard.ms_communication.application.service.exception.EmailSendException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.UnknownHostException;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class DynamicEmailSenderAdapter implements DynamicEmailSenderPort {

    private static final String USER_AGENT = "ms-communication";
    private static final Map<String, String> EMAIL_TAGS = Map.of(
            "provider", "dynamic-email-sender",
            "service", "ms-communication"
    );

    private final MetricsPort metricsPort;
    private final DynamicEmailSenderClient dynamicEmailSenderClient;

    @Override
    public EmailSendResponseDTO sendEmail(String url, EmailSendRequestDTO request) {
        log.info("Iniciando envio de email dinâmico para: {} via URL: {}", request.getTo(), url);

        var sample = metricsPort.startSample();
        URI sendUri = URI.create(trimTrailingSlash(url) + "/send/mail");

        try {
            EmailSendResponseDTO body = dynamicEmailSenderClient.sendMail(
                    sendUri,
                    request,
                    USER_AGENT,
                    MediaType.APPLICATION_JSON_VALUE);

            if (body == null) {
                metricsPort.recordError("email.send", "email.send.duration", "ms-communication", sample);
                metricsPort.incrementCounter("email.send.failure", EMAIL_TAGS);
                throw new EmailSendException("Falha no envio de email - resposta vazia");
            }

            metricsPort.recordSuccess("email.send", "email.send.duration", "ms-communication", sample);
            metricsPort.incrementCounter("email.send.success", EMAIL_TAGS);
            log.info("Email enviado com sucesso via URL: {}. MessageId: {}", url, body.getMessageId());
            return body;

        } catch (FeignException e) {
            metricsPort.recordError("email.send", "email.send.duration", "ms-communication", sample);
            metricsPort.incrementCounter("email.send.failure", EMAIL_TAGS);
            log.error("Erro HTTP no envio de email via URL: {}. Status: {}, Response: {}",
                    url, e.status(), e.contentUTF8(), e);
            throw new EmailSendException("Erro HTTP no envio de email: " + e.getMessage());

        } catch (EmailSendException e) {
            throw e;

        } catch (Exception e) {
            metricsPort.recordError("email.send", "email.send.duration", "ms-communication", sample);
            metricsPort.incrementCounter("email.send.failure", EMAIL_TAGS);

            if (isUnknownHost(e)) {
                String hostname = extractHostnameFromUrl(url);
                String errorMessage = String.format(
                        "Serviço de email não encontrado: '%s'. "
                                + "Verifique se o serviço está rodando e se a URL está correta para o ambiente atual. "
                                + "Para ambiente local, use 'localhost'; para Docker, use o nome do container.",
                        hostname);
                log.error("Erro de conectividade no envio de email via URL: {}. Causa: {}", url, e.getMessage(), e);
                throw new EmailSendException(errorMessage, e);
            }

            log.error("Erro inesperado no envio de email via URL: {}", url, e);
            throw new EmailSendException("Erro inesperado no envio de email: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean testConnection(String url) {
        log.info("Testando conexão com serviço de email via URL: {}", url);

        try {
            URI healthUri = URI.create(trimTrailingSlash(url) + "/health");
            String body = dynamicEmailSenderClient.health(healthUri, MediaType.APPLICATION_JSON_VALUE);
            boolean isHealthy = body != null && body.contains("UP");
            log.info("Teste de conexão via URL: {} - Status: {}", url, isHealthy ? "SUCCESS" : "FAILED");
            return isHealthy;
        } catch (Exception e) {
            log.warn("Falha no teste de conexão via URL: {}. Erro: {}", url, e.getMessage());
            return false;
        }
    }

    private static String trimTrailingSlash(String url) {
        if (url == null || url.isBlank()) {
            return "";
        }
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    private static boolean isUnknownHost(Throwable e) {
        Throwable cur = e;
        while (cur != null) {
            if (cur instanceof UnknownHostException) {
                return true;
            }
            cur = cur.getCause();
        }
        return false;
    }

    private String extractHostnameFromUrl(String url) {
        try {
            URI uri = URI.create(url);
            return uri.getHost() != null ? uri.getHost() : "hostname-desconhecido";
        } catch (Exception e) {
            return "url-inválida";
        }
    }
}
