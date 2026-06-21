package com.keepguard.ms_communication.domain.event;

import com.keepguard.lib_common.communication.enums.CommunicationTypeEnum;
import com.keepguard.lib_common.communication.enums.MessageTypeEnum;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Evento de domínio que representa a falha no envio de uma mensagem.
 * 
 * <p>Este evento é publicado quando uma mensagem falha ao ser enviada através
 * de um provedor de comunicação. Segue os princípios do DDD onde eventos
 * representam fatos que ocorreram no passado.</p>
 * 
 * <p>Características:</p>
 * <ul>
 *   <li>Imutável (Record - Java 21)</li>
 *   <li>Nome no passado (MessageFailed)</li>
 *   <li>Contém apenas dados relevantes ao evento</li>
 *   <li>Serializável para mensageria</li>
 * </ul>
 * 
 * @param attemptId ID único da tentativa de envio
 * @param providerId ID do provedor que falhou
 * @param providerName Nome do provedor que falhou
 * @param recipient Destinatário da mensagem
 * @param communicationType Tipo de comunicação (EMAIL, SMS, etc.)
 * @param messageType Tipo da mensagem (NOTIFICATION, ALERT, etc.)
 * @param errorMessage Mensagem de erro detalhada
 * @param errorCode Código de erro para categorização
 * @param failedAt Timestamp da falha
 * @param correlationId ID de correlação para rastreamento
 * 
 * @author KeepGuard Team
 * @version 1.1.2
 * @since 1.1.2
 */
public record MessageFailedEvent(
    UUID attemptId,
    UUID providerId,
    String providerName,
    String recipient,
    CommunicationTypeEnum communicationType,
    MessageTypeEnum messageType,
    String errorMessage,
    String errorCode,
    LocalDateTime failedAt,
    String correlationId
) implements DomainEvent {
    
    /**
     * Construtor de conveniência para criar evento com timestamp atual.
     * 
     * @param attemptId ID da tentativa
     * @param providerId ID do provedor
     * @param providerName Nome do provedor
     * @param recipient Destinatário
     * @param communicationType Tipo de comunicação
     * @param messageType Tipo da mensagem
     * @param errorMessage Mensagem de erro
     * @param errorCode Código de erro
     * @param correlationId ID de correlação
     * @return Evento com timestamp atual
     */
    public static MessageFailedEvent now(
            UUID attemptId,
            UUID providerId,
            String providerName,
            String recipient,
            CommunicationTypeEnum communicationType,
            MessageTypeEnum messageType,
            String errorMessage,
            String errorCode,
            String correlationId) {
        return new MessageFailedEvent(
            attemptId,
            providerId,
            providerName,
            recipient,
            communicationType,
            messageType,
            errorMessage,
            errorCode,
            LocalDateTime.now(),
            correlationId
        );
    }
}
