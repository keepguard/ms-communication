package com.keepguard.ms_communication.infrastructure.provider.communication;

import com.keepguard.lib_common.communication.enums.CommunicationTypeEnum;
import com.keepguard.ms_communication.adapters.out.sms.SmsSenderRabbitMQProducer;
import com.keepguard.ms_communication.adapters.out.sms.dto.SmsQueueMessageDTO;
import com.keepguard.ms_communication.domain.entity.Provider;
import com.keepguard.ms_communication.domain.enums.ProviderTypeEnum;
import com.keepguard.ms_communication.infrastructure.provider.CommunicationProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class SmsSenderCommunicationProvider implements CommunicationProvider {

    private final SmsSenderRabbitMQProducer smsSenderRabbitMQProducer;

    @Override
    public boolean sendMessage(Provider provider, String recipient, String subject, String content,
                              CommunicationTypeEnum communicationType, String messageType, String templateType) {

        log.info("Enviando SMS via SRV_SMS_SENDER para: {}", recipient);

        try {
            if (!supports(provider)) {
                log.error("Provider não suportado pelo SmsSenderCommunicationProvider: {}", provider.getProviderType());
                return false;
            }

            if (!CommunicationTypeEnum.SMS.equals(communicationType)) {
                log.error("Tipo de comunicação não suportado pelo SmsSenderCommunicationProvider: {}", communicationType);
                return false;
            }

            String correlationId = MDC.get("correlationId");
            String companyId = MDC.get("companyId");
            if (companyId == null || companyId.isBlank()) {
                companyId = MDC.get("X-Tenant-Id");
            }
            if (companyId == null || companyId.isBlank()) {
                companyId = "f7fc7350-b9fc-4e54-9c58-ac9385b23ae3";
            }
            if (correlationId == null || correlationId.isBlank()) {
                correlationId = UUID.randomUUID().toString();
            }

            SmsQueueMessageDTO smsMessage = SmsQueueMessageDTO.builder()
                    .id(UUID.randomUUID().toString())
                    .companyId(companyId)
                    .recipient(recipient)
                    .body(content != null ? content : subject)
                    .senderId(provider.getName())
                    .correlationId(correlationId)
                    .build();

            smsSenderRabbitMQProducer.publishSmsMessage(smsMessage);
            log.info("SMS publicado via RabbitMQ com sucesso para: {} (CorrelationId: {})", recipient, correlationId);
            return true;

        } catch (Exception e) {
            log.error("Falha ao enviar SMS via SRV_SMS_SENDER para: {} - Erro: {}", recipient, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean supports(Provider provider) {
        return provider != null && ProviderTypeEnum.SRV_SMS_SENDER.equals(provider.getProviderType());
    }

    @Override
    public boolean testConnection(Provider provider) {
        return smsSenderRabbitMQProducer.isConfigured();
    }
}
