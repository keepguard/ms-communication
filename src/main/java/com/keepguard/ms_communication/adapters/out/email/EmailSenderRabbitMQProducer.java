package com.keepguard.ms_communication.adapters.out.email;

import com.keepguard.ms_communication.adapters.out.email.dto.EmailMessageDTO;
import com.keepguard.ms_communication.infrastructure.messaging.rabbitmq.properties.RabbitMQProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * Producer RabbitMQ para envio de mensagens de email.
 * 
 * <p>Este componente é responsável por publicar mensagens de email
 * no RabbitMQ para que o serviço srv-email-google-sender possa consumir
 * e processar o envio dos emails.</p>
 * 
 * <p>Características:</p>
 * <ul>
 *   <li>Publicação assíncrona no RabbitMQ</li>
 *   <li>Configuração externalizada via properties</li>
 *   <li>Logging detalhado para observabilidade</li>
 *   <li>Tratamento de erros robusto</li>
 * </ul>
 * 
 * @author KeepGuard Team
 * @version 1.1.2
 * @since 1.1.2
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class EmailSenderRabbitMQProducer {
    
    private final RabbitTemplate rabbitTemplate;
    private final RabbitMQProperties rabbitMQProperties;
    
    /**
     * Publica uma mensagem de email no RabbitMQ.
     * 
     * @param message DTO contendo os dados do email a ser enviado
     * @throws RuntimeException se houver erro na publicação
     */
    public void publishEmailMessage(EmailMessageDTO message) {
        try {
            String exchange = rabbitMQProperties.getQueues().getEmailSenderExchange();
            String routingKey = rabbitMQProperties.getQueues().getEmailSenderRoutingKey();
            
            log.info("Publicando mensagem de email no RabbitMQ - Exchange: {}, RoutingKey: {}, To: {}", 
                exchange, routingKey, message.getTo());
            
            // Publicar mensagem no RabbitMQ
            rabbitTemplate.convertAndSend(exchange, routingKey, message);
            
            log.info("Mensagem de email publicada com sucesso no RabbitMQ para: {} (CorrelationId: {})", 
                message.getTo(), message.getXCorrelationId());
            
        } catch (Exception e) {
            log.error("Erro ao publicar mensagem de email no RabbitMQ para: {} (CorrelationId: {}) - Erro: {}", 
                message.getTo(), message.getXCorrelationId(), e.getMessage(), e);
            throw new RuntimeException("Falha ao publicar mensagem no RabbitMQ", e);
        }
    }
    
    /**
     * Verifica se o producer está configurado corretamente.
     * 
     * @return true se as configurações estão válidas
     */
    public boolean isConfigured() {
        String exchange = rabbitMQProperties.getQueues().getEmailSenderExchange();
        String routingKey = rabbitMQProperties.getQueues().getEmailSenderRoutingKey();
        
        return exchange != null && !exchange.trim().isEmpty() && 
               routingKey != null && !routingKey.trim().isEmpty();
    }
}
