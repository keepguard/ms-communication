package com.keepguard.ms_communication.infrastructure.provider.n8n;

import com.keepguard.lib_common.communication.enums.CommunicationTypeEnum;
import com.keepguard.ms_communication.infrastructure.provider.n8n.payload.*;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
@RequiredArgsConstructor
public class N8NHttpClient {

    private final RestTemplate restTemplate;

    @CircuitBreaker(name = "n8nClient")
    @Retry(name = "n8nClient")
    public ResponseEntity<String> sendEmail(String url, EmailPayload payload) {
        log.info("Enviando email via N8N para: {}", payload.getTo());

        HttpHeaders headers = createHeaders();
        HttpEntity<EmailPayload> request = new HttpEntity<>(payload, headers);

        return restTemplate.postForEntity(url, request, String.class);
    }

    @CircuitBreaker(name = "n8nClient")
    @Retry(name = "n8nClient")
    public ResponseEntity<String> sendSMS(String url, SMSPayload payload) {
        log.info("Enviando SMS via N8N para: {}", payload.getTo());

        HttpHeaders headers = createHeaders();
        HttpEntity<SMSPayload> request = new HttpEntity<>(payload, headers);

        return restTemplate.postForEntity(url, request, String.class);
    }

    @CircuitBreaker(name = "n8nClient")
    @Retry(name = "n8nClient")
    public ResponseEntity<String> sendWhatsApp(String url, WhatsAppPayload payload) {
        log.info("Enviando WhatsApp via N8N para: {}", payload.getChatId());

        HttpHeaders headers = createHeaders();
        HttpEntity<WhatsAppPayload> request = new HttpEntity<>(payload, headers);

        return restTemplate.postForEntity(url, request, String.class);
    }

    @CircuitBreaker(name = "n8nClient")
    @Retry(name = "n8nClient")
    public ResponseEntity<String> sendPush(String url, PushPayload payload) {
        log.info("Enviando push notification via N8N para: {}", payload.getTo());

        HttpHeaders headers = createHeaders();
        HttpEntity<PushPayload> request = new HttpEntity<>(payload, headers);

        return restTemplate.postForEntity(url, request, String.class);
    }

    @CircuitBreaker(name = "n8nClient")
    @Retry(name = "n8nClient")
    public ResponseEntity<String> testConnection(String url, CommunicationTypeEnum communicationType) {
        log.info("Testando conexão com N8N: {} - Tipo: {}", url, communicationType);

        TestPayload testPayload = TestPayload.builder()
                .test(true)
                .workflowType(communicationType.name().toLowerCase())
                .build();

        HttpHeaders headers = createHeaders();
        HttpEntity<TestPayload> request = new HttpEntity<>(testPayload, headers);

        return restTemplate.postForEntity(url, request, String.class);
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}