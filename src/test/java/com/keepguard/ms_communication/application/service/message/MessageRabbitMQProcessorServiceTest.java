package com.keepguard.ms_communication.application.service.message;

import com.keepguard.lib_common.communication.enums.CommunicationTypeEnum;
import com.keepguard.lib_common.communication.enums.MessageTypeEnum;
import com.keepguard.lib_common.communication.enums.TemplateTypeEnum;
import com.keepguard.ms_communication.adapters.in.messaging.rabbitmq.dto.MessageSendRabbitMQDTO;
import com.keepguard.ms_communication.adapters.in.messaging.rabbitmq.mapper.MessageRabbitMQMapper;
import com.keepguard.ms_communication.application.port.in.service.MessagePort;
import com.keepguard.ms_communication.application.port.out.messaging.EventPublisherPort;
import com.keepguard.ms_communication.domain.dto.message.MessageSendCommandDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MessageRabbitMQProcessorService Tests")
class MessageRabbitMQProcessorServiceTest {

    @Mock
    private MessageRabbitMQMapper messageRabbitMQMapper;

    @Mock
    private MessagePort messagePort;

    @Mock
    private EventPublisherPort eventPublisherPort;

    @InjectMocks
    private MessageRabbitMQProcessorService processorService;

    private MessageSendRabbitMQDTO validRabbitMQMessage;
    private MessageSendCommandDTO validCommand;

    @BeforeEach
    void setUp() {
        validRabbitMQMessage = new MessageSendRabbitMQDTO(
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

        validCommand = MessageSendCommandDTO.builder()
            .tenantId(java.util.UUID.randomUUID())
            .communicationType(CommunicationTypeEnum.EMAIL)
            .recipient("test@example.com")
            .subject("Test Subject")
            .content("Test Content")
            .messageType("EMAIL")
            .templateType("NOTIFICACAO_GERAL")
            .codeUser("user123")
            .variables(Map.of("name", "John"))
            .build();
    }

    @Test
    @DisplayName("Deve processar mensagem RabbitMQ com sucesso")
    void shouldProcessRabbitMQMessageSuccessfully() {
        // Given
        when(messageRabbitMQMapper.toSendCommand(validRabbitMQMessage)).thenReturn(validCommand);
        when(messagePort.sendWithFallback(validCommand)).thenReturn(true);

        // When
        processorService.processMessageSend(validRabbitMQMessage);

        // Then
        verify(messageRabbitMQMapper).toSendCommand(validRabbitMQMessage);
        verify(messagePort).sendWithFallback(validCommand);
        verifyNoInteractions(eventPublisherPort);
    }

    @Test
    @DisplayName("Deve processar mensagem RabbitMQ mesmo quando envio falha")
    void shouldProcessRabbitMQMessageEvenWhenSendingFails() {
        // Given
        when(messageRabbitMQMapper.toSendCommand(validRabbitMQMessage)).thenReturn(validCommand);
        when(messagePort.sendWithFallback(validCommand)).thenReturn(false);

        // When
        processorService.processMessageSend(validRabbitMQMessage);

        // Then
        verify(messageRabbitMQMapper).toSendCommand(validRabbitMQMessage);
        verify(messagePort).sendWithFallback(validCommand);
        verifyNoInteractions(eventPublisherPort);
    }

    @Test
    @DisplayName("Deve tratar exceção de validação IllegalArgumentException")
    void shouldHandleIllegalArgumentException() {
        // Given
        when(messageRabbitMQMapper.toSendCommand(validRabbitMQMessage))
            .thenThrow(new IllegalArgumentException("Mensagem inválida"));

        // When
        processorService.processMessageSend(validRabbitMQMessage);

        // Then
        verify(messageRabbitMQMapper).toSendCommand(validRabbitMQMessage);
        verifyNoInteractions(messagePort);
        verifyNoInteractions(eventPublisherPort);
    }

    @Test
    @DisplayName("Deve tratar exceção genérica durante processamento")
    void shouldHandleGenericExceptionDuringProcessing() {
        // Given
        when(messageRabbitMQMapper.toSendCommand(validRabbitMQMessage)).thenReturn(validCommand);
        when(messagePort.sendWithFallback(validCommand))
            .thenThrow(new RuntimeException("Erro interno"));

        // When
        processorService.processMessageSend(validRabbitMQMessage);

        // Then
        verify(messageRabbitMQMapper).toSendCommand(validRabbitMQMessage);
        verify(messagePort).sendWithFallback(validCommand);
        verifyNoInteractions(eventPublisherPort);
    }

    @Test
    @DisplayName("Deve processar mensagem com campos opcionais nulos")
    void shouldProcessMessageWithNullOptionalFields() {
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

        MessageSendCommandDTO commandWithNulls = MessageSendCommandDTO.builder()
            .tenantId(java.util.UUID.randomUUID())
            .communicationType(CommunicationTypeEnum.EMAIL)
            .recipient("test@example.com")
            .subject(null)
            .content(null)
            .messageType("EMAIL")
            .templateType("NOTIFICACAO_GERAL")
            .codeUser(null)
            .variables(null)
            .build();

        when(messageRabbitMQMapper.toSendCommand(messageWithNulls)).thenReturn(commandWithNulls);
        when(messagePort.sendWithFallback(commandWithNulls)).thenReturn(true);

        // When
        processorService.processMessageSend(messageWithNulls);

        // Then
        verify(messageRabbitMQMapper).toSendCommand(messageWithNulls);
        verify(messagePort).sendWithFallback(commandWithNulls);
        verifyNoInteractions(eventPublisherPort);
    }

    @Test
    @DisplayName("Deve processar mensagem com diferentes tipos de comunicação")
    void shouldProcessMessageWithDifferentCommunicationTypes() {
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

        MessageSendCommandDTO smsCommand = MessageSendCommandDTO.builder()
            .tenantId(java.util.UUID.randomUUID())
            .communicationType(CommunicationTypeEnum.SMS)
            .recipient("+5511999999999")
            .subject("SMS Subject")
            .content("SMS Content")
            .messageType("SMS")
            .templateType("AUTENTICACAO_SMS_TOKEN")
            .codeUser("user123")
            .variables(Map.of("code", "123456"))
            .build();

        when(messageRabbitMQMapper.toSendCommand(smsMessage)).thenReturn(smsCommand);
        when(messagePort.sendWithFallback(smsCommand)).thenReturn(true);

        // When
        processorService.processMessageSend(smsMessage);

        // Then
        verify(messageRabbitMQMapper).toSendCommand(smsMessage);
        verify(messagePort).sendWithFallback(smsCommand);
        verifyNoInteractions(eventPublisherPort);
    }

    @Test
    @DisplayName("Deve processar mensagem com variáveis complexas")
    void shouldProcessMessageWithComplexVariables() {
        // Given
        Map<String, Object> complexVariables = Map.of(
            "user", Map.of("name", "John", "age", 30),
            "company", "Test Corp",
            "settings", Map.of("theme", "dark", "notifications", true)
        );

        MessageSendRabbitMQDTO complexMessage = new MessageSendRabbitMQDTO(
            "test-app",
            "test-correlation-123",
            MessageTypeEnum.EMAIL,
            "test@example.com",
            TemplateTypeEnum.NOTIFICACAO_GERAL,
            "Complex Subject",
            "Complex Content",
            CommunicationTypeEnum.EMAIL,
            "user123",
            complexVariables
        );

        MessageSendCommandDTO complexCommand = MessageSendCommandDTO.builder()
            .tenantId(java.util.UUID.randomUUID())
            .communicationType(CommunicationTypeEnum.EMAIL)
            .recipient("test@example.com")
            .subject("Complex Subject")
            .content("Complex Content")
            .messageType("EMAIL")
            .templateType("NOTIFICACAO_GERAL")
            .codeUser("user123")
            .variables(complexVariables)
            .build();

        when(messageRabbitMQMapper.toSendCommand(complexMessage)).thenReturn(complexCommand);
        when(messagePort.sendWithFallback(complexCommand)).thenReturn(true);

        // When
        processorService.processMessageSend(complexMessage);

        // Then
        verify(messageRabbitMQMapper).toSendCommand(complexMessage);
        verify(messagePort).sendWithFallback(complexCommand);
        verifyNoInteractions(eventPublisherPort);
    }
}
