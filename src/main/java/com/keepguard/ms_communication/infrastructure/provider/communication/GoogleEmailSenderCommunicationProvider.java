package com.keepguard.ms_communication.infrastructure.provider.communication;

import com.keepguard.lib_common.communication.enums.CommunicationTypeEnum;
import com.keepguard.ms_communication.adapters.out.email.EmailSenderRabbitMQProducer;
import com.keepguard.ms_communication.adapters.out.email.dto.EmailMessageDTO;
import com.keepguard.ms_communication.adapters.out.email.dto.EmailSendRequestDTO;
import com.keepguard.ms_communication.adapters.out.email.dto.EmailSendResponseDTO;
import com.keepguard.ms_communication.application.port.out.email.DynamicEmailSenderPort;
import com.keepguard.ms_communication.application.service.exception.EmailSendException;
import com.keepguard.ms_communication.domain.entity.Provider;
import com.keepguard.ms_communication.domain.enums.ProviderTypeEnum;
import com.keepguard.ms_communication.infrastructure.provider.CommunicationProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class GoogleEmailSenderCommunicationProvider implements CommunicationProvider {

    private final DynamicEmailSenderPort dynamicEmailSenderPort;
    private final EmailSenderRabbitMQProducer emailSenderRabbitMQProducer;

    @Override
    public boolean sendMessage(Provider provider, String recipient, String subject, String content,
                              CommunicationTypeEnum communicationType, String messageType, String templateType) {
        
        log.info("Enviando mensagem via Google Email Sender para: {}", recipient);
        
        try {
            // Validações básicas
            if (!supports(provider)) {
                log.error("Provider não suportado pelo Google Email Sender: {}", provider.getProviderType());
                return false;
            }
            
            if (!CommunicationTypeEnum.EMAIL.equals(communicationType)) {
                log.error("Tipo de comunicação não suportado pelo Google Email Sender: {}", communicationType);
                return false;
            }

            // PRIMEIRO: Tentar via RabbitMQ (método principal)
            try {
                if (emailSenderRabbitMQProducer.isConfigured()) {
                    EmailMessageDTO emailMessage = EmailMessageDTO.builder()
                        .to(recipient)
                        .subject(subject)
                        .html(content)
                        .companyId(currentTenantId())
                        .xCorrelationId(UUID.randomUUID().toString())
                        .build();
                    
                    emailSenderRabbitMQProducer.publishEmailMessage(emailMessage);
                    log.info("Mensagem publicada via RabbitMQ para: {} (CorrelationId: {})", 
                        recipient, emailMessage.getXCorrelationId());
                    return true;
                } else {
                    log.warn("EmailSenderRabbitMQProducer não está configurado, tentando fallback HTTP");
                }
            } catch (Exception rabbitMqError) {
                log.warn("Falha ao publicar via RabbitMQ, tentando fallback HTTP: {}", 
                    rabbitMqError.getMessage());
            }
            
            // FALLBACK: Usar HTTP se RabbitMQ falhar ou não estiver configurado
            if (provider.getUrl() != null && !provider.getUrl().trim().isEmpty()) {
                try {
                    EmailSendRequestDTO request = EmailSendRequestDTO.builder()
                        .to(recipient)
                        .subject(subject)
                        .html(content)
                        .build();
                    
                    EmailSendResponseDTO response = dynamicEmailSenderPort.sendEmail(provider.getUrl(), request);
                    
                    if (response != null && response.getMessageId() != null) {
                        log.info("Mensagem enviada via HTTP fallback. MessageId: {}", 
                            response.getMessageId());
                        return true;
                    } else {
                        log.error("Falha no envio de mensagem via HTTP fallback - resposta inválida");
                        return false;
                    }
                } catch (EmailSendException e) {
                    log.error("Erro no envio de mensagem via HTTP fallback para: {} - URL: {} - Erro: {}", 
                             recipient, provider.getUrl(), e.getMessage(), e);
                    return false;
                } catch (Exception e) {
                    log.error("Erro inesperado no envio de mensagem via HTTP fallback para: {} - URL: {} - Erro: {}", 
                             recipient, provider.getUrl(), e.getMessage(), e);
                    return false;
                }
            } else {
                log.error("URL do provider não configurada para fallback HTTP: {}", provider.getName());
                return false;
            }
            
        } catch (Exception e) {
            log.error("Erro inesperado no envio de mensagem via Google Email Sender para: {} - Erro: {}", 
                     recipient, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean supports(Provider provider) {
        return provider != null && 
               ProviderTypeEnum.EMAIL_GOOGLE_SENDER.equals(provider.getProviderType());
    }

    private String currentTenantId() {
        try {
            var attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                String header = attrs.getRequest().getHeader("X-Tenant-Id");
                if (header != null && !header.isBlank()) {
                    return header.trim();
                }
            }
        } catch (Exception ignored) {
            // fora de request HTTP
        }
        return "keepguard-guardian";
    }

    @Override
    public boolean testConnection(Provider provider) {
        if (!supports(provider)) {
            log.warn("Provider não suportado para teste de conexão: {}", 
                    provider != null ? provider.getProviderType() : "null");
            return false;
        }
        
        // Validação da URL do provider
        if (provider.getUrl() == null || provider.getUrl().trim().isEmpty()) {
            log.warn("URL do provider não configurada para teste de conexão: {}", provider.getName());
            return false;
        }
        
        log.info("Testando conexão com Google Email Sender via URL: {}", provider.getUrl());
        return dynamicEmailSenderPort.testConnection(provider.getUrl());
    }
}
