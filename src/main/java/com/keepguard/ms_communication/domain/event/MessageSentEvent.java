package com.keepguard.ms_communication.domain.event;

import com.keepguard.lib_common.communication.enums.CommunicationTypeEnum;
import com.keepguard.lib_common.communication.enums.MessageTypeEnum;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Evento de domínio que representa o envio bem-sucedido de uma mensagem.
 * 
 * <p>Este evento é publicado quando uma mensagem é enviada com sucesso através
 * de um provedor de comunicação. Segue os princípios do DDD onde eventos
 * representam fatos que ocorreram no passado.</p>
 * 
 * <p>Características:</p>
 * <ul>
 *   <li>Imutável (Record - Java 21)</li>
 *   <li>Nome no passado (MessageSent)</li>
 *   <li>Contém apenas dados relevantes ao evento</li>
 *   <li>Serializável para mensageria</li>
 * </ul>
 * 
 * @param messageId ID único da mensagem enviada
 * @param providerId ID do provedor utilizado
 * @param providerName Nome do provedor utilizado
 * @param recipient Destinatário da mensagem
 * @param communicationType Tipo de comunicação (EMAIL, SMS, etc.)
 * @param messageType Tipo da mensagem (NOTIFICATION, ALERT, etc.)
 * @param sentAt Timestamp do envio
 * @param correlationId ID de correlação para rastreamento
 * 
 * @author KeepGuard Team
 * @version 1.1.2
 * @since 1.1.2
 */
public record MessageSentEvent(
    UUID messageId,
    UUID providerId,
    String providerName,
    String recipient,
    CommunicationTypeEnum communicationType,
    MessageTypeEnum messageType,
    LocalDateTime sentAt,
    String correlationId
) implements DomainEvent {
    
    /**
     * Construtor de conveniência para criar evento com timestamp atual.
     * 
     * @param messageId ID único da mensagem
     * @param providerId ID do provedor
     * @param providerName Nome do provedor
     * @param recipient Destinatário
     * @param communicationType Tipo de comunicação
     * @param messageType Tipo da mensagem
     * @param correlationId ID de correlação
     * @return Evento com timestamp atual
     */
    public static MessageSentEvent now(
            UUID messageId,
            UUID providerId,
            String providerName,
            String recipient,
            CommunicationTypeEnum communicationType,
            MessageTypeEnum messageType,
            String correlationId) {
        return new MessageSentEvent(
            messageId,
            providerId,
            providerName,
            recipient,
            communicationType,
            messageType,
            LocalDateTime.now(),
            correlationId
        );
    }
}
