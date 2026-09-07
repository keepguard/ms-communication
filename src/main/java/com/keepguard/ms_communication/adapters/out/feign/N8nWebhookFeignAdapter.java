package com.keepguard.ms_communication.adapters.out.feign;

import com.keepguard.lib_common.communication.enums.CommunicationTypeEnum;
import com.keepguard.ms_communication.infrastructure.provider.n8n.payload.EmailPayload;
import com.keepguard.ms_communication.infrastructure.provider.n8n.payload.PushPayload;
import com.keepguard.ms_communication.infrastructure.provider.n8n.payload.SMSPayload;
import com.keepguard.ms_communication.infrastructure.provider.n8n.payload.TestPayload;
import com.keepguard.ms_communication.infrastructure.provider.n8n.payload.WhatsAppPayload;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.net.URI;

@Component
@Slf4j
@RequiredArgsConstructor
public class N8nWebhookFeignAdapter {

    private final N8nWebhookClient n8nWebhookClient;

    @CircuitBreaker(name = "n8nClient")
    @Retry(name = "n8nClient")
    public ResponseEntity<String> sendEmail(String url, EmailPayload payload) {
        log.info("Enviando email via N8N para: {}", payload.getTo());
        return n8nWebhookClient.post(URI.create(url), payload);
    }

    @CircuitBreaker(name = "n8nClient")
    @Retry(name = "n8nClient")
    public ResponseEntity<String> sendSMS(String url, SMSPayload payload) {
        log.info("Enviando SMS via N8N para: {}", payload.getTo());
        return n8nWebhookClient.post(URI.create(url), payload);
    }

    @CircuitBreaker(name = "n8nClient")
    @Retry(name = "n8nClient")
    public ResponseEntity<String> sendWhatsApp(String url, WhatsAppPayload payload) {
        log.info("Enviando WhatsApp via N8N para: {}", payload.getChatId());
        return n8nWebhookClient.post(URI.create(url), payload);
    }

    @CircuitBreaker(name = "n8nClient")
    @Retry(name = "n8nClient")
    public ResponseEntity<String> sendPush(String url, PushPayload payload) {
        log.info("Enviando push notification via N8N para: {}", payload.getTo());
        return n8nWebhookClient.post(URI.create(url), payload);
    }

    @CircuitBreaker(name = "n8nClient")
    @Retry(name = "n8nClient")
    public ResponseEntity<String> testConnection(String url, CommunicationTypeEnum communicationType) {
        log.info("Testando conexão com N8N: {} - Tipo: {}", url, communicationType);
        TestPayload testPayload = TestPayload.builder()
                .test(true)
                .workflowType(communicationType.name().toLowerCase())
                .build();
        return n8nWebhookClient.post(URI.create(url), testPayload);
    }
}
