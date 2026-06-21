package com.keepguard.ms_communication.application.port.in.messaging;

import com.keepguard.ms_communication.adapters.in.messaging.rabbitmq.dto.MessageSendRabbitMQDTO;

public interface MessageSendRabbitMQPort {
    
    /**
     * Processa uma mensagem de envio recebida via RabbitMQ.
     */
    void processMessageSend(MessageSendRabbitMQDTO rabbitMQMessage);
}
