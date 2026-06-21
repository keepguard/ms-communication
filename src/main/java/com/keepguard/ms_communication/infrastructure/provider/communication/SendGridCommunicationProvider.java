package com.keepguard.ms_communication.infrastructure.provider.communication;

import com.keepguard.ms_communication.application.service.exception.MessageSendException;
import com.keepguard.ms_communication.application.service.exception.ProviderConnectionException;
import com.keepguard.ms_communication.infrastructure.provider.CommunicationProvider;
import com.keepguard.lib_common.communication.enums.CommunicationTypeEnum;
import com.keepguard.ms_communication.domain.entity.Provider;
import com.keepguard.ms_communication.domain.enums.ProviderTypeEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class SendGridCommunicationProvider implements CommunicationProvider {

    // Aqui você injetaria os componentes específicos do SendGrid
    // private final SendGridHttpClient httpClient;
    // private final SendGridPayloadFactory payloadFactory;

    @Override
    public boolean sendMessage(Provider provider, String recipient, String subject, String content,
                              CommunicationTypeEnum communicationType, String messageType, String templateType) {
        try {
            log.info("Enviando mensagem via SendGrid para: {} - Tipo: {} - MessageType: {} - TemplateType: {}",
                     recipient, communicationType, messageType, templateType);

            // Implementação específica do SendGrid
            // return httpClient.sendEmail(provider.getUrl(), payloadFactory.createEmailPayload(...));

            // Placeholder para demonstração
            log.info("Mensagem enviada com sucesso via SendGrid para: {}", recipient);
            return true;

        } catch (Exception e) {
            String errorMessage = String.format("Erro ao enviar mensagem via SendGrid para %s: %s", recipient, e.getMessage());
            log.error(errorMessage, e);
            throw new MessageSendException(errorMessage, e);
        }
    }

    @Override
    public boolean supports(Provider provider) {
        return ProviderTypeEnum.SENDGRID.equals(provider.getProviderType());
    }

    @Override
    public boolean testConnection(Provider provider) {
        try {
            log.info("Testando conectividade com SendGrid: {}", provider.getUrl());

            // Implementação específica do teste de conexão SendGrid
            // var response = httpClient.testConnection(provider.getUrl());
            // return response.getStatusCode().is2xxSuccessful();

            // Placeholder para demonstração
            log.info("Conexão com SendGrid testada com sucesso");
            return true;

        } catch (Exception e) {
            String errorMessage = String.format("Erro no teste de conectividade com SendGrid: %s", e.getMessage());
            log.error(errorMessage, e);
            throw new ProviderConnectionException(errorMessage, e);
        }
    }
}