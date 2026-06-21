package com.keepguard.ms_communication.infrastructure.provider.n8n;

import com.keepguard.ms_communication.domain.entity.Provider;
import com.keepguard.ms_communication.infrastructure.provider.n8n.payload.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PayloadFactory {

    private final N8NConfigParser configParser;

    public EmailPayload createEmailPayload(Provider providerDomain, String recipient, String subject, String content) {
        return EmailPayload.builder()
            .to(recipient)
            .subject(subject)
            .message("")
            .html(content)
            .cc("")
            .replyTo("")
            .build();
    }

    public SMSPayload createSMSPayload(Provider providerDomain, String recipient, String content) {
        return SMSPayload.builder()
            .to(recipient)
            .message(content)
            .workflowType("sms")
            .build();
    }

    public WhatsAppPayload createWhatsAppPayload(Provider providerDomain, String recipient, String content, String messageType, String templateType) {
        return WhatsAppPayload.builder()
            .chatId(recipient)
            .text(content)
            .messageType(messageType)
            .templateType(templateType)
            .build();
    }

    public PushPayload createPushPayload(Provider providerDomain, String recipient, String subject, String content) {
        return PushPayload.builder()
            .to(recipient)
            .title(subject)
            .message(content)
            .workflowType("push")
            .build();
    }
}