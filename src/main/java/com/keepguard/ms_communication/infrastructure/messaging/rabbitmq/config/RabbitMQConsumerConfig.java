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
    public org.springframework.amqp.rabbit.retry.MessageRecoverer communicationMessageRecoverer(
            org.springframework.amqp.rabbit.core.RabbitTemplate rabbitTemplate,
            com.keepguard.ms_communication.infrastructure.messaging.rabbitmq.properties.RabbitMQProperties rabbitMQProperties) {
        
        var recoverer = new org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer(
                rabbitTemplate,
                rabbitMQProperties.getQueues().getDeadLetterExchange(),
                rabbitMQProperties.getQueues().getRoutingKeyMessageFailed()
        );
        recoverer.setErrorHeaderName("x-exception-message");
        return recoverer;
    }

    /**
     * Factory para listeners RabbitMQ com Retry Resiliente e Dead Letter Forense.
     */
    @Bean
    public RabbitListenerContainerFactory<?> rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory, 
            MessageConverter messageConverter,
            org.springframework.amqp.rabbit.retry.MessageRecoverer communicationMessageRecoverer) {
        
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        factory.setAcknowledgeMode(org.springframework.amqp.core.AcknowledgeMode.MANUAL);
        
        var retryTemplate = new org.springframework.retry.support.RetryTemplate();
        var backOffPolicy = new org.springframework.retry.backoff.ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(1000);
        backOffPolicy.setMultiplier(2.0);
        backOffPolicy.setMaxInterval(5000);
        retryTemplate.setBackOffPolicy(backOffPolicy);

        var retryPolicy = new org.springframework.retry.policy.SimpleRetryPolicy(3);
        retryTemplate.setRetryPolicy(retryPolicy);

        var advice = org.springframework.amqp.rabbit.config.RetryInterceptorBuilder.stateless()
                .retryOperations(retryTemplate)
                .recoverer(communicationMessageRecoverer)
                .build();

        factory.setAdviceChain(advice);
        return factory;
    }
}
