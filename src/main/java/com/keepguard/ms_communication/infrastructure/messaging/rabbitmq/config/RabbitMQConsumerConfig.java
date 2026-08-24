package com.keepguard.ms_communication.infrastructure.messaging.rabbitmq.config;

import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração do Consumer RabbitMQ.
 * 
 * <p>Esta classe configura o Consumer RabbitMQ seguindo as melhores práticas
 * de consumo e processamento de mensagens em ambientes distribuídos.</p>
 * 
 * <p>Características:</p>
 * <ul>
 *   <li>Configuração externalizada via properties</li>
 *   <li>Deserialização JSON para eventos</li>
 *   <li>Configurações de commit manual</li>
 *   <li>Otimizações de performance</li>
 * </ul>
 * 
 * @author KeepGuard Team
 * @version 1.1.2
 * @since 1.1.2
 */
@Configuration
public class RabbitMQConsumerConfig {
    
    
    /**
     * Factory para listeners RabbitMQ.
     * 
     * @param connectionFactory ConnectionFactory do RabbitMQ
     * @param messageConverter MessageConverter injetado
     * @return RabbitListenerContainerFactory configurada
     */
    @Bean
    public RabbitListenerContainerFactory<?> rabbitListenerContainerFactory(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        factory.setAcknowledgeMode(org.springframework.amqp.core.AcknowledgeMode.MANUAL);
        
        return factory;
    }
}
