package com.keepguard.ms_communication.infrastructure.messaging.rabbitmq.config;

import com.keepguard.ms_communication.infrastructure.messaging.rabbitmq.properties.RabbitMQProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RabbitMQProducerConfig Tests")
class RabbitMQProducerConfigTest {

    @Mock
    private CachingConnectionFactory connectionFactory;

    @Mock
    private RabbitMQProperties rabbitMQProperties;

    @Mock
    private RabbitMQProperties.Queues queues;

    private RabbitMQProducerConfig config;

    @BeforeEach
    void setUp() {
        config = new RabbitMQProducerConfig(rabbitMQProperties);
    }

    @Test
    @DisplayName("Deve criar MessageConverter corretamente")
    void shouldCreateMessageConverterCorrectly() {
        // When
        MessageConverter converter = config.jsonMessageConverter();

        // Then
        assertNotNull(converter);
        assertTrue(converter instanceof org.springframework.amqp.support.converter.Jackson2JsonMessageConverter);
    }

    @Test
    @DisplayName("Deve criar RabbitTemplate com configurações corretas")
    void shouldCreateRabbitTemplateWithCorrectConfigurations() {
        // When
        RabbitTemplate template = config.rabbitTemplate(connectionFactory);

        // Then
        assertNotNull(template);
        assertEquals(connectionFactory, template.getConnectionFactory());
        assertNotNull(template.getMessageConverter());
    }

    @Test
    @DisplayName("Deve criar Exchange de mensagens corretamente")
    void shouldCreateMessageExchangeCorrectly() {
        // Given
        when(rabbitMQProperties.getQueues()).thenReturn(queues);
        when(queues.getMessageExchange()).thenReturn("ms-communication-exchange-local");
        when(queues.getDurable()).thenReturn(true);
        when(queues.getAutoDelete()).thenReturn(false);
        
        // When
        TopicExchange exchange = config.messageExchange();

        // Then
        assertNotNull(exchange);
        assertEquals("ms-communication-exchange-local", exchange.getName());
        assertTrue(exchange.isDurable());
        assertFalse(exchange.isAutoDelete());
    }

    @Test
    @DisplayName("Deve criar Exchange de dead letter corretamente")
    void shouldCreateDeadLetterExchangeCorrectly() {
        // Given
        when(rabbitMQProperties.getQueues()).thenReturn(queues);
        when(queues.getDeadLetterExchange()).thenReturn("dead-letter-exchange");
        when(queues.getDurable()).thenReturn(true);
        when(queues.getAutoDelete()).thenReturn(false);
        
        // When
        DirectExchange exchange = config.deadLetterExchange();

        // Then
        assertNotNull(exchange);
        assertEquals("dead-letter-exchange", exchange.getName());
        assertTrue(exchange.isDurable());
        assertFalse(exchange.isAutoDelete());
    }

    @Test
    @DisplayName("Deve criar fila de requisições de envio corretamente")
    void shouldCreateMessageSendRequestsQueueCorrectly() {
        // Given
        when(rabbitMQProperties.getQueues()).thenReturn(queues);
        when(queues.getMessageSend()).thenReturn("message-send-requests-queue");
        when(queues.getDeadLetterExchange()).thenReturn("dead-letter-exchange");
        
        // When
        Queue queue = config.messageSendRequestsQueue();

        // Then
        assertNotNull(queue);
        assertEquals("message-send-requests-queue", queue.getName());
        assertTrue(queue.isDurable());
        assertFalse(queue.isExclusive());
        assertFalse(queue.isAutoDelete());
    }


    @Test
    @DisplayName("Deve criar fila de dead letter corretamente")
    void shouldCreateDeadLetterQueueCorrectly() {
        // Given
        when(rabbitMQProperties.getQueues()).thenReturn(queues);
        when(queues.getMessageSendRequestsDlt()).thenReturn("dead-letter-queue");
        
        // When
        Queue queue = config.deadLetterQueue();

        // Then
        assertNotNull(queue);
        assertEquals("dead-letter-queue", queue.getName());
        assertTrue(queue.isDurable());
        assertFalse(queue.isExclusive());
        assertFalse(queue.isAutoDelete());
    }

    @Test
    @DisplayName("Deve criar binding de requisições de envio corretamente")
    void shouldCreateMessageSendRequestsBindingCorrectly() {
        // Given
        when(rabbitMQProperties.getQueues()).thenReturn(queues);
        when(queues.getMessageExchange()).thenReturn("ms-communication-exchange-local");
        when(queues.getMessageSend()).thenReturn("message-send-requests-queue");
        when(queues.getDeadLetterExchange()).thenReturn("dead-letter-exchange");
        when(queues.getDurable()).thenReturn(true);
        when(queues.getAutoDelete()).thenReturn(false);
        when(queues.getRoutingKeyMessageSend()).thenReturn("communication.message.send");
        
        // When
        Binding binding = config.messageSendRequestsBinding();

        // Then
        assertNotNull(binding);
        assertEquals("communication.message.send", binding.getRoutingKey());
        assertEquals("ms-communication-exchange-local", binding.getExchange());
        assertEquals("message-send-requests-queue", binding.getDestination());
    }


    @Test
    @DisplayName("Deve criar binding de dead letter corretamente")
    void shouldCreateDeadLetterBindingCorrectly() {
        // Given
        when(rabbitMQProperties.getQueues()).thenReturn(queues);
        when(queues.getDeadLetterExchange()).thenReturn("dead-letter-exchange");
        when(queues.getMessageSendRequestsDlt()).thenReturn("dead-letter-queue");
        when(queues.getDurable()).thenReturn(true);
        when(queues.getAutoDelete()).thenReturn(false);
        when(queues.getRoutingKeyMessageFailed()).thenReturn("failed");
        
        // When
        Binding binding = config.deadLetterBinding();

        // Then
        assertNotNull(binding);
        assertEquals("failed", binding.getRoutingKey());
        assertEquals("dead-letter-exchange", binding.getExchange());
        assertEquals("dead-letter-queue", binding.getDestination());
    }
}