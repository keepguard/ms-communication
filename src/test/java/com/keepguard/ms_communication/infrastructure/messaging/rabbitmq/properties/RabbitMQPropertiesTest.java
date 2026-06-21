package com.keepguard.ms_communication.infrastructure.messaging.rabbitmq.properties;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("RabbitMQProperties Tests")
class RabbitMQPropertiesTest {

    private RabbitMQProperties properties;

    @BeforeEach
    void setUp() {
        properties = new RabbitMQProperties();
        properties.setHost("localhost");
        properties.setPort(5672);
        properties.setUsername("guest");
        properties.setPassword("guest");
        properties.setVirtualHost("/");
        
        // Publisher properties
        RabbitMQProperties.Publisher publisher = new RabbitMQProperties.Publisher();
        publisher.setConfirmType(true);
        publisher.setMandatory(true);
        properties.setPublisher(publisher);
        
        // Consumer properties
        RabbitMQProperties.Consumer consumer = new RabbitMQProperties.Consumer();
        consumer.setPrefetchCount(1);
        consumer.setAutoAck(false);
        consumer.setConcurrency(2);
        consumer.setMaxConcurrency(10);
        properties.setConsumer(consumer);
        
        // Queues properties
        RabbitMQProperties.Queues queues = new RabbitMQProperties.Queues();
        queues.setMessageSend("message-send-requests-queue");
        queues.setMessageSendRequestsDlt("dead-letter-queue");
        queues.setMessageExchange("ms-communication-exchange-local");
        queues.setDeadLetterExchange("dead-letter-exchange");
        properties.setQueues(queues);
    }

    @Test
    @DisplayName("Deve configurar propriedades básicas corretamente")
    void shouldConfigureBasicPropertiesCorrectly() {
        // Then
        assertEquals("localhost", properties.getHost());
        assertEquals(5672, properties.getPort());
        assertEquals("guest", properties.getUsername());
        assertEquals("guest", properties.getPassword());
        assertEquals("/", properties.getVirtualHost());
    }

    @Test
    @DisplayName("Deve configurar propriedades do publisher corretamente")
    void shouldConfigurePublisherPropertiesCorrectly() {
        // Given
        RabbitMQProperties.Publisher publisher = properties.getPublisher();

        // Then
        assertNotNull(publisher);
        assertTrue(publisher.getConfirmType());
        assertTrue(publisher.getMandatory());
    }

    @Test
    @DisplayName("Deve configurar propriedades do consumer corretamente")
    void shouldConfigureConsumerPropertiesCorrectly() {
        // Given
        RabbitMQProperties.Consumer consumer = properties.getConsumer();

        // Then
        assertNotNull(consumer);
        assertEquals(1, consumer.getPrefetchCount());
        assertFalse(consumer.getAutoAck());
        assertEquals(2, consumer.getConcurrency());
        assertEquals(10, consumer.getMaxConcurrency());
    }

    @Test
    @DisplayName("Deve configurar nomes das filas corretamente")
    void shouldConfigureQueueNamesCorrectly() {
        // Given
        RabbitMQProperties.Queues queues = properties.getQueues();

        // Then
        assertNotNull(queues);
        assertEquals("message-send-requests-queue", queues.getMessageSend());
        assertEquals("dead-letter-queue", queues.getMessageSendRequestsDlt());
        assertEquals("ms-communication-exchange-local", queues.getMessageExchange());
        assertEquals("dead-letter-exchange", queues.getDeadLetterExchange());
    }

    @Test
    @DisplayName("Deve criar instância com valores padrão")
    void shouldCreateInstanceWithDefaultValues() {
        // When
        RabbitMQProperties defaultProperties = new RabbitMQProperties();

        // Then
        assertEquals("localhost", defaultProperties.getHost());
        assertEquals(5672, defaultProperties.getPort());
        assertEquals("guest", defaultProperties.getUsername());
        assertEquals("guest", defaultProperties.getPassword());
        assertEquals("/", defaultProperties.getVirtualHost());
        assertNotNull(defaultProperties.getPublisher());
        assertNotNull(defaultProperties.getConsumer());
        assertNotNull(defaultProperties.getQueues());
    }

    @Test
    @DisplayName("Deve configurar valores extremos")
    void shouldConfigureExtremeValues() {
        // Given
        properties.setHost("a".repeat(255));
        properties.setPort(65535);
        properties.setUsername("u".repeat(100));
        properties.setPassword("p".repeat(100));
        properties.setVirtualHost("v".repeat(100));

        // Then
        assertEquals("a".repeat(255), properties.getHost());
        assertEquals(65535, properties.getPort());
        assertEquals("u".repeat(100), properties.getUsername());
        assertEquals("p".repeat(100), properties.getPassword());
        assertEquals("v".repeat(100), properties.getVirtualHost());
    }

    @Test
    @DisplayName("Deve configurar consumer com valores extremos")
    void shouldConfigureConsumerWithExtremeValues() {
        // Given
        RabbitMQProperties.Consumer consumer = new RabbitMQProperties.Consumer();
        consumer.setPrefetchCount(Integer.MAX_VALUE);
        consumer.setConcurrency(Integer.MAX_VALUE);
        consumer.setMaxConcurrency(Integer.MAX_VALUE);

        // When
        properties.setConsumer(consumer);

        // Then
        assertEquals(Integer.MAX_VALUE, properties.getConsumer().getPrefetchCount());
        assertEquals(Integer.MAX_VALUE, properties.getConsumer().getConcurrency());
        assertEquals(Integer.MAX_VALUE, properties.getConsumer().getMaxConcurrency());
    }
}
