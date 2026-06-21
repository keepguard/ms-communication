package com.keepguard.ms_communication.infrastructure.messaging.rabbitmq.adapter;

import com.keepguard.ms_communication.application.port.out.messaging.EventPublisherPort;
import com.keepguard.ms_communication.domain.event.MessageFailedEvent;
import com.keepguard.ms_communication.domain.event.MessageSentEvent;
import com.keepguard.ms_communication.infrastructure.messaging.rabbitmq.properties.RabbitMQProperties;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RabbitMQEventPublisherAdapter implements EventPublisherPort {
    
    private final RabbitTemplate rabbitTemplate;
    private final RabbitMQProperties rabbitMQProperties;
    
    /**
     * Publica evento de mensagem enviada com sucesso.
     */
    @Override
    @CircuitBreaker(name = "rabbitMQProducer", fallbackMethod = "fallbackPublishMessageSent")
    @Retry(name = "rabbitMQProducer")
    public void publishMessageSentEvent(MessageSentEvent event) {
        log.debug("Publicando evento de mensagem enviada: messageId={}, providerId={}, recipient={}", 
            event.messageId(), event.providerId(), event.recipient());
        
        try {
            String routingKey = "message.sent";
            String exchange = rabbitMQProperties.getQueues().getMessageExchange();
            
            rabbitTemplate.convertAndSend(exchange, routingKey, event);
            
            log.info("Evento de mensagem enviada publicado com sucesso: messageId={}", 
                event.messageId());
            
        } catch (Exception e) {
            log.error("Falha ao publicar evento de mensagem enviada: messageId={}, error={}", 
                event.messageId(), e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Publica evento de falha no envio de mensagem.
     */
    @Override
    @CircuitBreaker(name = "rabbitMQProducer", fallbackMethod = "fallbackPublishMessageFailed")
    @Retry(name = "rabbitMQProducer")
    public void publishMessageFailedEvent(MessageFailedEvent event) {
        log.debug("Publicando evento de falha no envio: attemptId={}, providerId={}, recipient={}", 
            event.attemptId(), event.providerId(), event.recipient());
        
        try {
            String routingKey = "message.failed";
            String exchange = rabbitMQProperties.getQueues().getMessageExchange();
            
            rabbitTemplate.convertAndSend(exchange, routingKey, event);
            
            log.info("Evento de falha no envio publicado com sucesso: attemptId={}", 
                event.attemptId());
            
        } catch (Exception e) {
            log.error("Falha ao publicar evento de falha no envio: attemptId={}, error={}", 
                event.attemptId(), e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Fallback para falha na publicação de evento de mensagem enviada.
     */
    public void fallbackPublishMessageSent(MessageSentEvent event, Exception ex) {
        log.error("FALLBACK: Falha crítica ao publicar evento de mensagem enviada: messageId={}, error={}", 
            event.messageId(), ex.getMessage());
        // Aqui poderia implementar fallback para banco de dados ou DLQ
    }
    
    /**
     * Fallback para falha na publicação de evento de falha no envio.
     */
    public void fallbackPublishMessageFailed(MessageFailedEvent event, Exception ex) {
        log.error("FALLBACK: Falha crítica ao publicar evento de falha no envio: attemptId={}, error={}", 
            event.attemptId(), ex.getMessage());
        // Aqui poderia implementar fallback para banco de dados ou DLQ
    }
}
