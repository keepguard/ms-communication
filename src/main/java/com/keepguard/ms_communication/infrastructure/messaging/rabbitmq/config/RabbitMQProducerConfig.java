package com.keepguard.ms_communication.infrastructure.messaging.rabbitmq.config;

import com.keepguard.ms_communication.infrastructure.messaging.rabbitmq.properties.RabbitMQProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import lombok.extern.slf4j.Slf4j;

/**
 * Configuração do Producer RabbitMQ.
 * 
 * <p>Esta classe configura o Producer RabbitMQ seguindo as melhores práticas
 * de produção e resiliência para ambientes distribuídos.</p>
 * 
 * <p>Características:</p>
 * <ul>
 *   <li>Configuração externalizada via properties</li>
 *   <li>Serialização JSON para eventos</li>
 *   <li>Configurações de confirmação e retry</li>
 *   <li>Dead Letter Queue para mensagens falhadas</li>
 * </ul>
 * 
 * @author KeepGuard Team
 * @version 1.1.2
 * @since 1.1.2
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class RabbitMQProducerConfig {
    
    private final RabbitMQProperties rabbitMQProperties;
    
    /**
     * Configuração do MessageConverter para JSON.
     * 
     * @return MessageConverter configurado
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    
    /**
     * Configuração do RabbitTemplate.
     * 
     * @param connectionFactory ConnectionFactory do RabbitMQ
     * @return RabbitTemplate configurado
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        
        // Configurações de confirmação
        template.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.debug("Message sent successfully: {}", correlationData);
            } else {
                log.error("Message failed to send: {}", cause);
            }
        });
        
        // Configurações de retorno
        template.setReturnsCallback(returned -> {
            log.error("Message returned: {}", returned.getMessage());
        });
        
        template.setMandatory(true);
        
        return template;
    }
    
    /**
     * Exchange principal para mensagens.
     * 
     * @return TopicExchange configurado
     */
    @Bean
    public TopicExchange messageExchange() {
        return new TopicExchange(rabbitMQProperties.getQueues().getMessageExchange(), 
                                rabbitMQProperties.getQueues().getDurable(), 
                                rabbitMQProperties.getQueues().getAutoDelete());
    }
    
    /**
     * Dead Letter Exchange para mensagens falhadas.
     * 
     * @return DirectExchange configurado
     */
    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(rabbitMQProperties.getQueues().getDeadLetterExchange(), 
                                 rabbitMQProperties.getQueues().getDurable(), 
                                 rabbitMQProperties.getQueues().getAutoDelete());
    }
    
    /**
     * Fila principal para requisições de envio de mensagem.
     * 
     * @return Queue configurada
     */
    @Bean
    public Queue messageSendRequestsQueue() {
        return QueueBuilder.durable(rabbitMQProperties.getQueues().getMessageSend())
                .withArgument("x-dead-letter-exchange", rabbitMQProperties.getQueues().getDeadLetterExchange())
                .withArgument("x-dead-letter-routing-key", "failed")
                .build();
    }
    
    
    /**
     * Fila de Dead Letter para mensagens falhadas.
     * 
     * @return Queue configurada
     */
    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(rabbitMQProperties.getQueues().getMessageSendRequestsDlt())
                .withArgument("x-message-ttl", 604800000)
                .build();
    }
    
    /**
     * Binding da fila de requisições com o exchange.
     * 
     * @return Binding configurado
     */
    @Bean
    public Binding messageSendRequestsBinding() {
        return BindingBuilder
                .bind(messageSendRequestsQueue())
                .to(messageExchange())
                .with(rabbitMQProperties.getQueues().getRoutingKeyMessageSend());
    }
    
    
    /**
     * Binding da Dead Letter Queue.
     * 
     * @return Binding configurado
     */
    @Bean
    public Binding deadLetterBinding() {
        return BindingBuilder
                .bind(deadLetterQueue())
                .to(deadLetterExchange())
                .with(rabbitMQProperties.getQueues().getRoutingKeyMessageFailed());
    }
    
    /**
     * Exchange para publicar mensagens no srv-email-google-sender.
     * 
     * @return TopicExchange configurado
     */
    @Bean
    public TopicExchange emailSenderExchange() {
        return new TopicExchange(
            rabbitMQProperties.getQueues().getEmailSenderExchange(), 
            rabbitMQProperties.getQueues().getDurable(), 
            rabbitMQProperties.getQueues().getAutoDelete()
        );
    }
}
