package com.keepguard.ms_communication.infrastructure.messaging.rabbitmq.config;

import com.keepguard.ms_communication.infrastructure.messaging.rabbitmq.properties.RabbitMQProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class BillingInvoiceRabbitConfig {

    private final RabbitMQProperties rabbitMQProperties;

    @Value("${rabbitmq.queues.billing-invoice:ms.communication.billing.invoice.local}")
    private String billingInvoiceQueue;

    @Value("${rabbitmq.billing-exchange:ms-billing-exchange-local}")
    private String billingExchangeName;

    @Bean
    public TopicExchange billingFactsExchange() {
        return new TopicExchange(billingExchangeName, true, false);
    }

    @Bean
    public Queue billingInvoiceQueue() {
        return QueueBuilder.durable(billingInvoiceQueue)
                .withArgument("x-dead-letter-exchange", rabbitMQProperties.getQueues().getDeadLetterExchange())
                .withArgument("x-dead-letter-routing-key", rabbitMQProperties.getQueues().getRoutingKeyMessageFailed())
                .build();
    }

    @Bean
    public Binding billingInvoicePaidBinding() {
        return BindingBuilder.bind(billingInvoiceQueue())
                .to(billingFactsExchange())
                .with("billing.invoice.paid");
    }

    @Bean
    public Binding billingInvoiceOverdueBinding() {
        return BindingBuilder.bind(billingInvoiceQueue())
                .to(billingFactsExchange())
                .with("billing.invoice.overdue");
    }
}
