package com.keepguard.ms_communication.adapters.in.messaging.rabbitmq.consumer;

import com.keepguard.lib_common.communication.enums.CommunicationTypeEnum;
import com.keepguard.lib_common.communication.enums.MessageTypeEnum;
import com.keepguard.lib_common.communication.enums.TemplateTypeEnum;
import com.keepguard.ms_communication.adapters.in.messaging.rabbitmq.dto.MessageSendRabbitMQDTO;
import com.keepguard.ms_communication.application.port.in.messaging.MessageSendRabbitMQPort;
import com.rabbitmq.client.Channel;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.retry.Retry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;

import java.lang.reflect.Method;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MessageSendRabbitMQConsumer Tests")
class MessageSendRabbitMQConsumerTest {

    @Mock
    private MessageSendRabbitMQPort messageSendRabbitMQPort;

    @Mock
    private Channel channel;

    @Mock
    private CircuitBreaker circuitBreaker;

    @Mock
    private Retry retry;

    @InjectMocks
    private MessageSendRabbitMQConsumer consumer;

    private MessageSendRabbitMQDTO validMessage;
    private long deliveryTag = 12345L;

    @BeforeEach
    void setUp() {
        validMessage = new MessageSendRabbitMQDTO(
            "test-app",
            "test-correlation-123",
            MessageTypeEnum.EMAIL,
            "test@example.com",
            TemplateTypeEnum.NOTIFICACAO_GERAL,
            "Test Subject",
            "Test Content",
            CommunicationTypeEnum.EMAIL,
            "user123",
            Map.of("name", "John")
        );
    }

    @Test
    @DisplayName("Deve ter anotação @RabbitListener configurada corretamente")
    void shouldHaveRabbitListenerAnnotationConfigured() throws NoSuchMethodException {
        // Given
        Method consumeMethod = MessageSendRabbitMQConsumer.class
            .getMethod("consumeMessageSend", MessageSendRabbitMQDTO.class, long.class, Channel.class);

        // When
        RabbitListener annotation = consumeMethod.getAnnotation(RabbitListener.class);

        // Then
        assertNotNull(annotation);
        assertEquals("${rabbitmq.queues.message-send}", annotation.queues()[0]);
    }

    @Test
    @DisplayName("Deve processar mensagem válida com sucesso")
    void shouldProcessValidMessageSuccessfully() throws Exception {
        // Given
        doNothing().when(messageSendRabbitMQPort).processMessageSend(validMessage);
        doNothing().when(channel).basicAck(deliveryTag, false);

        // When
        consumer.consumeMessageSend(validMessage, deliveryTag, channel);

        // Then
        verify(messageSendRabbitMQPort).processMessageSend(validMessage);
        verify(channel).basicAck(deliveryTag, false);
    }

    @Test
    @DisplayName("Deve tratar exceção de validação e rejeitar para DLQ")
    void shouldHandleValidationExceptionAndRejectToDLQ() throws Exception {
        // Given
        IllegalArgumentException validationException = new IllegalArgumentException("Mensagem inválida");
        doThrow(validationException).when(messageSendRabbitMQPort).processMessageSend(validMessage);
        doNothing().when(channel).basicNack(deliveryTag, false, false);

        // When
        consumer.consumeMessageSend(validMessage, deliveryTag, channel);

        // Then
        verify(messageSendRabbitMQPort).processMessageSend(validMessage);
        verify(channel).basicNack(deliveryTag, false, false);
    }

    @Test
    @DisplayName("Deve tratar exceção genérica e rejeitar mensagem para retry")
    void shouldHandleGenericExceptionAndRejectMessageForRetry() throws Exception {
        // Given
        RuntimeException genericException = new RuntimeException("Erro interno");
        doThrow(genericException).when(messageSendRabbitMQPort).processMessageSend(validMessage);
        doNothing().when(channel).basicNack(deliveryTag, false, true);

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            consumer.consumeMessageSend(validMessage, deliveryTag, channel);
        });

        verify(messageSendRabbitMQPort).processMessageSend(validMessage);
        verify(channel).basicNack(deliveryTag, false, true);
    }

    @Test
    @DisplayName("Deve tratar IOException ao rejeitar mensagem inválida para DLQ")
    void shouldHandleIOExceptionWhenRejectingInvalidMessageToDLQ() throws Exception {
        // Given
        IllegalArgumentException validationException = new IllegalArgumentException("Mensagem inválida");
        java.io.IOException ioException = new java.io.IOException("Erro de IO");
        
        doThrow(validationException).when(messageSendRabbitMQPort).processMessageSend(validMessage);
        doThrow(ioException).when(channel).basicNack(deliveryTag, false, false);

        // When
        consumer.consumeMessageSend(validMessage, deliveryTag, channel);

        // Then
        verify(messageSendRabbitMQPort).processMessageSend(validMessage);
        verify(channel).basicNack(deliveryTag, false, false);
    }

    @Test
    @DisplayName("Deve tratar IOException ao rejeitar mensagem para retry")
    void shouldHandleIOExceptionWhenRejectingMessageForRetry() throws Exception {
        // Given
        RuntimeException genericException = new RuntimeException("Erro interno");
        java.io.IOException ioException = new java.io.IOException("Erro de IO");
        
        doThrow(genericException).when(messageSendRabbitMQPort).processMessageSend(validMessage);
        doThrow(ioException).when(channel).basicNack(deliveryTag, false, true);

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            consumer.consumeMessageSend(validMessage, deliveryTag, channel);
        });

        verify(messageSendRabbitMQPort).processMessageSend(validMessage);
        verify(channel).basicNack(deliveryTag, false, true);
    }

    @Test
    @DisplayName("Deve tratar exceção genérica ao rejeitar mensagem inválida para DLQ")
    void shouldHandleGenericExceptionWhenRejectingInvalidMessageToDLQ() throws Exception {
        // Given
        IllegalArgumentException validationException = new IllegalArgumentException("Mensagem inválida");
        RuntimeException nackException = new RuntimeException("Erro ao rejeitar para DLQ");
        
        doThrow(validationException).when(messageSendRabbitMQPort).processMessageSend(validMessage);
        doThrow(nackException).when(channel).basicNack(deliveryTag, false, false);

        // When
        consumer.consumeMessageSend(validMessage, deliveryTag, channel);

        // Then
        verify(messageSendRabbitMQPort).processMessageSend(validMessage);
        verify(channel).basicNack(deliveryTag, false, false);
    }

    @Test
    @DisplayName("Deve tratar exceção genérica ao rejeitar mensagem para retry")
    void shouldHandleGenericExceptionWhenRejectingMessageForRetry() throws Exception {
        // Given
        RuntimeException genericException = new RuntimeException("Erro interno");
        RuntimeException nackException = new RuntimeException("Erro ao rejeitar");
        
        doThrow(genericException).when(messageSendRabbitMQPort).processMessageSend(validMessage);
        doThrow(nackException).when(channel).basicNack(deliveryTag, false, true);

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            consumer.consumeMessageSend(validMessage, deliveryTag, channel);
        });

        verify(messageSendRabbitMQPort).processMessageSend(validMessage);
        verify(channel).basicNack(deliveryTag, false, true);
    }

    @Test
    @DisplayName("Deve executar fallback para falhas críticas")
    void shouldExecuteFallbackForCriticalFailures() throws Exception {
        // Given
        RuntimeException criticalException = new RuntimeException("Falha crítica");
        doNothing().when(channel).basicNack(deliveryTag, false, false);

        // When
        consumer.fallbackProcessMessage(validMessage, deliveryTag, channel, criticalException);

        // Then
        verify(channel).basicNack(deliveryTag, false, false);
    }

    @Test
    @DisplayName("Deve tratar IOException no fallback")
    void shouldHandleIOExceptionInFallback() throws Exception {
        // Given
        RuntimeException criticalException = new RuntimeException("Falha crítica");
        java.io.IOException ioException = new java.io.IOException("Erro de IO");
        doThrow(ioException).when(channel).basicNack(deliveryTag, false, false);

        // When
        consumer.fallbackProcessMessage(validMessage, deliveryTag, channel, criticalException);

        // Then
        verify(channel).basicNack(deliveryTag, false, false);
    }

    @Test
    @DisplayName("Deve tratar exceção genérica no fallback")
    void shouldHandleGenericExceptionInFallback() throws Exception {
        // Given
        RuntimeException criticalException = new RuntimeException("Falha crítica");
        RuntimeException nackException = new RuntimeException("Erro ao rejeitar para DLQ");
        doThrow(nackException).when(channel).basicNack(deliveryTag, false, false);

        // When
        consumer.fallbackProcessMessage(validMessage, deliveryTag, channel, criticalException);

        // Then
        verify(channel).basicNack(deliveryTag, false, false);
    }

    @Test
    @DisplayName("Deve processar mensagem com campos nulos")
    void shouldProcessMessageWithNullFields() throws Exception {
        // Given
        MessageSendRabbitMQDTO messageWithNulls = new MessageSendRabbitMQDTO(
            "test-app",
            "test-correlation-123",
            MessageTypeEnum.EMAIL,
            "test@example.com",
            TemplateTypeEnum.NOTIFICACAO_GERAL,
            null, // subject null
            null, // content null
            CommunicationTypeEnum.EMAIL,
            null, // codeUser null
            null  // variables null
        );

        doNothing().when(messageSendRabbitMQPort).processMessageSend(messageWithNulls);
        doNothing().when(channel).basicAck(deliveryTag, false);

        // When
        consumer.consumeMessageSend(messageWithNulls, deliveryTag, channel);

        // Then
        verify(messageSendRabbitMQPort).processMessageSend(messageWithNulls);
        verify(channel).basicAck(deliveryTag, false);
    }

    @Test
    @DisplayName("Deve processar mensagem com diferentes tipos de comunicação")
    void shouldProcessMessageWithDifferentCommunicationTypes() throws Exception {
        // Given
        MessageSendRabbitMQDTO smsMessage = new MessageSendRabbitMQDTO(
            "test-app",
            "test-correlation-123",
            MessageTypeEnum.SMS,
            "+5511999999999",
            TemplateTypeEnum.AUTENTICACAO_SMS_TOKEN,
            "SMS Subject",
            "SMS Content",
            CommunicationTypeEnum.SMS,
            "user123",
            Map.of("code", "123456")
        );

        doNothing().when(messageSendRabbitMQPort).processMessageSend(smsMessage);
        doNothing().when(channel).basicAck(deliveryTag, false);

        // When
        consumer.consumeMessageSend(smsMessage, deliveryTag, channel);

        // Then
        verify(messageSendRabbitMQPort).processMessageSend(smsMessage);
        verify(channel).basicAck(deliveryTag, false);
    }
}
