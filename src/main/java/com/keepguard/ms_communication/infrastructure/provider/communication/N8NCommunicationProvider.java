package com.keepguard.ms_communication.infrastructure.provider.communication;

import com.keepguard.ms_communication.application.service.exception.MessageSendException;
import com.keepguard.ms_communication.application.service.exception.ProviderConnectionException;
import com.keepguard.ms_communication.infrastructure.provider.CommunicationProvider;
import com.keepguard.lib_common.communication.enums.CommunicationTypeEnum;
import com.keepguard.ms_communication.domain.entity.Provider;
import com.keepguard.ms_communication.domain.enums.ProviderTypeEnum;
import com.keepguard.ms_communication.infrastructure.provider.n8n.N8NConfigParser;
import com.keepguard.ms_communication.infrastructure.provider.n8n.N8NHttpClient;
import com.keepguard.ms_communication.infrastructure.provider.n8n.PayloadFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class N8NCommunicationProvider implements CommunicationProvider {

    private final N8NHttpClient httpClient;
    private final PayloadFactory payloadFactory;
    private final N8NConfigParser configParser;

    @Override
    public boolean sendMessage(Provider provider, String recipient, String subject, String content,
                             CommunicationTypeEnum communicationType, String messageType, String templateType) {
        try {
            log.info("Enviando mensagem via N8N para: {} - Tipo: {}", recipient, communicationType);

            boolean sent = sendMessageByType(provider, recipient, subject, content, communicationType, messageType, templateType);
            if (!sent) {
                String errorMessage = String.format("Falha ao enviar mensagem via N8N para: %s", recipient);
                log.error(errorMessage);
                throw new MessageSendException(errorMessage);
            }

            log.info("Mensagem enviada com sucesso via N8N para: {}", recipient);
            return true;

        } catch (Exception e) {
            String errorMessage = String.format("Erro ao enviar mensagem via N8N para %s: %s", recipient, e.getMessage());
            log.error(errorMessage, e);
            throw new MessageSendException(errorMessage, e);
        }
    }

    @Override
    public boolean supports(Provider provider) {
        return ProviderTypeEnum.N8N.equals(provider.getProviderType());
    }

    @Override
    public boolean testConnection(Provider provider) {
        try {
            log.info("Testando conectividade com N8N: {}", provider.getUrl());

            // Usar o tipo de comunicação do provider para teste
            CommunicationTypeEnum communicationType = provider.getCommunicationType();

            var response = httpClient.testConnection(provider.getUrl(), communicationType);
            boolean isConnected = response.getStatusCode().is2xxSuccessful();

            if (!isConnected) {
                String errorMessage = String.format("Falha no teste de conectividade com N8N: %s", provider.getUrl());
                log.error(errorMessage);
                throw new ProviderConnectionException(errorMessage);
            }

            log.info("Conexão com N8N testada com sucesso: {}", provider.getUrl());
            return true;

        } catch (Exception e) {
            String errorMessage = String.format("Erro no teste de conectividade com N8N: %s", e.getMessage());
            log.error(errorMessage, e);
            throw new ProviderConnectionException(errorMessage, e);
        }
    }

    private boolean sendMessageByType(Provider provider, String recipient, String subject,
                                    String content, CommunicationTypeEnum communicationType, String messageType, String templateType) {
        return switch (communicationType) {
            case EMAIL -> httpClient.sendEmail(provider.getUrl(),
                    payloadFactory.createEmailPayload(provider, recipient, subject, content))
                    .getStatusCode().is2xxSuccessful();
            case SMS -> httpClient.sendSMS(provider.getUrl(),
                    payloadFactory.createSMSPayload(provider, recipient, content))
                    .getStatusCode().is2xxSuccessful();
            case WHATSAPP -> httpClient.sendWhatsApp(provider.getUrl(),
                    payloadFactory.createWhatsAppPayload(provider, recipient, content, messageType, templateType))
                    .getStatusCode().is2xxSuccessful();
            case PUSH_NOTIFICATION -> httpClient.sendPush(provider.getUrl(),
                    payloadFactory.createPushPayload(provider, recipient, subject, content))
                    .getStatusCode().is2xxSuccessful();
            case PUSH -> httpClient.sendPush(provider.getUrl(),
                    payloadFactory.createPushPayload(provider, recipient, subject, content))
                    .getStatusCode().is2xxSuccessful();
            case SENDGRID -> httpClient.sendEmail(provider.getUrl(),
                    payloadFactory.createEmailPayload(provider, recipient, subject, content))
                    .getStatusCode().is2xxSuccessful();
            case TELEGRAM -> {
                log.warn("Telegram não suportado pelo N8N, usando WhatsApp como fallback");
                yield httpClient.sendWhatsApp(provider.getUrl(),
                        payloadFactory.createWhatsAppPayload(provider, recipient, content, messageType, templateType))
                        .getStatusCode().is2xxSuccessful();
            }
        };
    }
}
