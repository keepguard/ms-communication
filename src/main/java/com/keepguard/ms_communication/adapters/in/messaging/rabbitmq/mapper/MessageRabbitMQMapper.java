package com.keepguard.ms_communication.adapters.in.messaging.rabbitmq.mapper;

import com.keepguard.ms_communication.adapters.in.messaging.rabbitmq.dto.MessageSendRabbitMQDTO;
import com.keepguard.ms_communication.domain.dto.message.MessageSendCommandDTO;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Mapper para conversão entre DTOs RabbitMQ e Commands de domínio.
 * 
 * <p>Este mapper converte mensagens RabbitMQ para comandos de domínio,
 * seguindo os princípios da Arquitetura Hexagonal.</p>
 * 
 * <p>Características:</p>
 * <ul>
 *   <li>Conversão unidirecional RabbitMQ → Domain</li>
 *   <li>Mapeamento de campos específicos</li>
 *   <li>Validação durante conversão</li>
 *   <li>Tratamento de valores nulos</li>
 * </ul>
 * 
 * @author KeepGuard Team
 * @version 1.1.2
 * @since 1.1.2
 */
@Component
public class MessageRabbitMQMapper {
    
    /**
     * Converte DTO RabbitMQ para Command de envio de mensagem.
     * 
     * @param rabbitMQMessage DTO RabbitMQ
     * @return Command de domínio
     * @throws IllegalArgumentException se o DTO for inválido
     */
    public MessageSendCommandDTO toSendCommand(MessageSendRabbitMQDTO rabbitMQMessage) {
        if (rabbitMQMessage == null) {
            throw new IllegalArgumentException("Mensagem RabbitMQ não pode ser nula");
        }
        
        if (!rabbitMQMessage.isValid()) {
            throw new IllegalArgumentException("Mensagem RabbitMQ inválida: " + rabbitMQMessage.toLogString());
        }
        
        return MessageSendCommandDTO.builder()
            .companyId(UUID.fromString(rabbitMQMessage.companyId()))
            .communicationType(rabbitMQMessage.communicationType())
            .recipient(rabbitMQMessage.recipient())
            .subject(rabbitMQMessage.subject())
            .content(rabbitMQMessage.content())
            .messageType(rabbitMQMessage.messageType() != null ? rabbitMQMessage.messageType().toString() : null)
            .templateType(rabbitMQMessage.templateType() != null ? rabbitMQMessage.templateType().toString() : null)
            .codeUser(rabbitMQMessage.codeUser())
            .variables(rabbitMQMessage.variables())
            .build();
    }
}
