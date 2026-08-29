package com.keepguard.ms_communication.adapters.out.sms;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.keepguard.ms_communication.adapters.out.sms.dto.SmsQueueMessageDTO;
import com.keepguard.ms_communication.infrastructure.messaging.rabbitmq.properties.RabbitMQProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * Producer RabbitMQ para envio de mensagens de SMS.
 * Publica na fila keepguard.notifications.sms consumida pelo srv-sms-sender.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SmsSenderRabbitMQProducer {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitMQProperties rabbitMQProperties;
    private final ObjectMapper objectMapper;

    public boolean isConfigured() {
        return rabbitTemplate != null;
    }

    public void publishSmsMessage(SmsQueueMessageDTO message) {
        try {
            String queueName = rabbitMQProperties.getQueues().getSmsQueue();
            if (queueName == null || queueName.isBlank()) {
                queueName = "keepguard.notifications.sms";
            }

            log.info("Publicando mensagem de SMS no RabbitMQ - Queue: {}, Recipient: {}, CorrelationId: {}",
                    queueName, message.getRecipient(), message.getCorrelationId());

            String jsonPayload = objectMapper.writeValueAsString(message);

            Message rabbitMsg = MessageBuilder.withBody(jsonPayload.getBytes())
                    .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                    .setHeader("X-Correlation-ID", message.getCorrelationId())
                    .setHeader("correlationId", message.getCorrelationId())
                    .build();

            // Publica diretamente na fila padrão usando a default exchange ""
            rabbitTemplate.send("", queueName, rabbitMsg);

            log.info("Mensagem de SMS publicada com sucesso no RabbitMQ para: {} (Queue: {})",
                    message.getRecipient(), queueName);

        } catch (Exception e) {
            log.error("Erro ao publicar mensagem de SMS no RabbitMQ para: {} (CorrelationId: {}) - Erro: {}",
                    message.getRecipient(), message.getCorrelationId(), e.getMessage(), e);
            throw new RuntimeException("Falha ao publicar mensagem de SMS no RabbitMQ", e);
        }
    }
}
