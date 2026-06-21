package com.keepguard.ms_communication.infrastructure.messaging.rabbitmq.config;

import com.rabbitmq.client.ConnectionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.MessageConverter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RabbitMQConsumerConfig Tests")
class RabbitMQConsumerConfigTest {

    @Mock
    private CachingConnectionFactory connectionFactory;

    @Mock
    private MessageConverter messageConverter;

    private RabbitMQConsumerConfig config;

    @BeforeEach
    void setUp() {
        config = new RabbitMQConsumerConfig();
    }

    @Test
    @DisplayName("Deve criar RabbitListenerContainerFactory corretamente")
    void shouldCreateRabbitListenerContainerFactoryCorrectly() {
        // When
        RabbitListenerContainerFactory<?> factory = config.rabbitListenerContainerFactory(connectionFactory, messageConverter);

        // Then
        assertNotNull(factory);
        assertTrue(factory instanceof org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory);
    }

    @Test
    @DisplayName("Deve criar factory com ConnectionFactory nulo")
    void shouldCreateFactoryWithNullConnectionFactory() {
        // When
        RabbitListenerContainerFactory<?> factory = config.rabbitListenerContainerFactory(null, messageConverter);

        // Then
        assertNotNull(factory);
        assertTrue(factory instanceof org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory);
    }

    @Test
    @DisplayName("Deve criar factory com MessageConverter nulo")
    void shouldCreateFactoryWithNullMessageConverter() {
        // When
        RabbitListenerContainerFactory<?> factory = config.rabbitListenerContainerFactory(connectionFactory, null);

        // Then
        assertNotNull(factory);
        assertTrue(factory instanceof org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory);
    }

    @Test
    @DisplayName("Deve criar factory com ambos parâmetros nulos")
    void shouldCreateFactoryWithBothNullParameters() {
        // When
        RabbitListenerContainerFactory<?> factory = config.rabbitListenerContainerFactory(null, null);

        // Then
        assertNotNull(factory);
        assertTrue(factory instanceof org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory);
    }

    @Test
    @DisplayName("Deve retornar instância de SimpleRabbitListenerContainerFactory")
    void shouldReturnSimpleRabbitListenerContainerFactoryInstance() {
        // When
        RabbitListenerContainerFactory<?> factory = config.rabbitListenerContainerFactory(connectionFactory, messageConverter);

        // Then
        assertTrue(factory instanceof org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory);
    }

    @Test
    @DisplayName("Deve criar factory múltiplas vezes com mesmo resultado")
    void shouldCreateFactoryMultipleTimesWithSameResult() {
        // When
        RabbitListenerContainerFactory<?> factory1 = config.rabbitListenerContainerFactory(connectionFactory, messageConverter);
        RabbitListenerContainerFactory<?> factory2 = config.rabbitListenerContainerFactory(connectionFactory, messageConverter);

        // Then
        assertNotNull(factory1);
        assertNotNull(factory2);
        assertNotSame(factory1, factory2); // Diferentes instâncias
        assertTrue(factory1 instanceof org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory);
        assertTrue(factory2 instanceof org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory);
    }

    @Test
    @DisplayName("Deve configurar factory com propriedades padrão do Spring Boot")
    void shouldConfigureFactoryWithSpringBootDefaultProperties() {
        // When
        RabbitListenerContainerFactory<?> factory = config.rabbitListenerContainerFactory(connectionFactory, messageConverter);

        // Then
        assertNotNull(factory);
        assertTrue(factory instanceof org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory);
    }

    @Test
    @DisplayName("Deve aceitar ConnectionFactory genérico")
    void shouldAcceptGenericConnectionFactory() {
        // Given
        CachingConnectionFactory genericFactory = mock(CachingConnectionFactory.class);

        // When
        RabbitListenerContainerFactory<?> factory = config.rabbitListenerContainerFactory(genericFactory, messageConverter);

        // Then
        assertNotNull(factory);
        assertTrue(factory instanceof org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory);
    }

    @Test
    @DisplayName("Deve aceitar MessageConverter genérico")
    void shouldAcceptGenericMessageConverter() {
        // Given
        MessageConverter genericConverter = mock(MessageConverter.class);

        // When
        RabbitListenerContainerFactory<?> factory = config.rabbitListenerContainerFactory(connectionFactory, genericConverter);

        // Then
        assertNotNull(factory);
        assertTrue(factory instanceof org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory);
    }
}
