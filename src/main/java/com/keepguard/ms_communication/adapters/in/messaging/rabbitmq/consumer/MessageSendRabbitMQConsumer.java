package com.keepguard.ms_communication.adapters.in.messaging.rabbitmq.consumer;

import com.keepguard.ms_communication.adapters.in.messaging.rabbitmq.dto.MessageSendRabbitMQDTO;
import com.keepguard.ms_communication.application.port.in.messaging.MessageSendRabbitMQPort;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.rabbitmq.client.Channel;

@Component
@RequiredArgsConstructor
@Slf4j
public class MessageSendRabbitMQConsumer {
    
    private final MessageSendRabbitMQPort messageSendRabbitMQPort;

    @RabbitListener(
        queues = "${rabbitmq.queues.message-send}",
        containerFactory = "rabbitListenerContainerFactory"
    )
    @CircuitBreaker(name = "rabbitMQMessageProcessor", fallbackMethod = "fallbackProcessMessage")
    @Retry(name = "rabbitMQMessageProcessor")
    public void consumeMessageSend(
            @Payload MessageSendRabbitMQDTO rabbitMQMessage,
            @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag,
            Channel channel) throws Exception {
        
        log.info("Mensagem de envio recebida: xCorrelationId={}, recipient={}, deliveryTag={}", 
            rabbitMQMessage.xCorrelationId(), rabbitMQMessage.recipient(), deliveryTag);
        
        try {
            if (rabbitMQMessage.xCorrelationId() != null) {
                org.slf4j.MDC.put("correlationId", rabbitMQMessage.xCorrelationId());
                org.slf4j.MDC.put("traceId", rabbitMQMessage.xCorrelationId());
            }
            if (rabbitMQMessage.tenantId() != null) {
                org.slf4j.MDC.put("tenantId", rabbitMQMessage.tenantId());
                org.slf4j.MDC.put("X-Tenant-Id", rabbitMQMessage.tenantId());
            }

            // Validar mensagem recebida
            validateReceivedMessage(rabbitMQMessage);
            
            // Processar mensagem através do service
            messageSendRabbitMQPort.processMessageSend(rabbitMQMessage);
            
            // Confirmar processamento da mensagem
            channel.basicAck(deliveryTag, false);
            
            log.info("Mensagem de envio processada com sucesso: xCorrelationId={}, deliveryTag={}", 
                rabbitMQMessage.xCorrelationId(), deliveryTag);
            
        } catch (IllegalArgumentException e) {
            // Erro de validação - enviar direto para DLQ
            log.warn("Mensagem rejeitada por validação: xCorrelationId={}, error={}, deliveryTag={}", 
                rabbitMQMessage.xCorrelationId(), e.getMessage(), deliveryTag);
            
            try {
                // Rejeitar mensagem para enviar para DLQ (nack sem requeue)
                channel.basicNack(deliveryTag, false, false);
                log.info("Mensagem de validação enviada para DLQ: xCorrelationId={}, deliveryTag={}", 
                    rabbitMQMessage.xCorrelationId(), deliveryTag);
            } catch (java.io.IOException nackEx) {
                log.error("Erro de IO ao rejeitar mensagem de validação para DLQ: deliveryTag={}", deliveryTag, nackEx);
            } catch (Exception nackEx) {
                log.error("Erro ao rejeitar mensagem de validação para DLQ: deliveryTag={}", deliveryTag, nackEx);
            }
            
            // Não relançar a exceção para evitar retry
            return;
            
        } catch (Exception e) {
            // Erro de processamento - não confirmar ack para retry
            log.error("Erro no processamento de mensagem: xCorrelationId={}, error={}, deliveryTag={}", 
                rabbitMQMessage.xCorrelationId(), e.getMessage(), deliveryTag, e);
            
            try {
                // Rejeitar mensagem para retry (nack com requeue)
                channel.basicNack(deliveryTag, false, true);
            } catch (java.io.IOException nackEx) {
                log.error("Erro de IO ao rejeitar mensagem para retry: deliveryTag={}", deliveryTag, nackEx);
            } catch (Exception nackEx) {
                log.error("Erro ao rejeitar mensagem para retry: deliveryTag={}", deliveryTag, nackEx);
            }
            
            throw e;
        }
    }
    
    /**
     * Fallback para falhas críticas no processamento.
     */
    public void fallbackProcessMessage(MessageSendRabbitMQDTO rabbitMQMessage, long deliveryTag, 
                                      Channel channel, Exception ex) {
        log.error("FALLBACK: Falha crítica no processamento de mensagem: xCorrelationId={}, error={}, deliveryTag={}", 
            rabbitMQMessage.xCorrelationId(), ex.getMessage(), deliveryTag, ex);
        
        try {
            // Em caso de falha crítica, rejeitar mensagem para enviar para DLQ
            channel.basicNack(deliveryTag, false, false);
            log.info("Mensagem enviada para DLQ após falha crítica: xCorrelationId={}, deliveryTag={}", 
                rabbitMQMessage.xCorrelationId(), deliveryTag);
        } catch (java.io.IOException nackEx) {
            log.error("Erro de IO ao rejeitar mensagem no fallback: deliveryTag={}", deliveryTag, nackEx);
        } catch (Exception nackEx) {
            log.error("Erro ao rejeitar mensagem no fallback: deliveryTag={}", deliveryTag, nackEx);
        }
    }
    
    /**
     * Valida a mensagem recebida antes do processamento.
     */
    private void validateReceivedMessage(MessageSendRabbitMQDTO rabbitMQMessage) {
        if (rabbitMQMessage == null) {
            throw new IllegalArgumentException("Mensagem RabbitMQ não pode ser nula");
        }
        
        if (!rabbitMQMessage.isValid()) {
            throw new IllegalArgumentException("Mensagem RabbitMQ inválida: " + rabbitMQMessage.toLogString());
        }
        
        log.debug("Validação da mensagem recebida concluída: xCorrelationId={}", rabbitMQMessage.xCorrelationId());
    }
}
