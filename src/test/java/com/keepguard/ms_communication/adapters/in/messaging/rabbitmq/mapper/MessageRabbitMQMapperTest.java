package com.keepguard.ms_communication.adapters.in.messaging.rabbitmq.mapper;

import com.keepguard.lib_common.communication.enums.CommunicationTypeEnum;
import com.keepguard.lib_common.communication.enums.MessageTypeEnum;
import com.keepguard.lib_common.communication.enums.TemplateTypeEnum;
import com.keepguard.ms_communication.adapters.in.messaging.rabbitmq.dto.MessageSendRabbitMQDTO;
import com.keepguard.ms_communication.domain.dto.message.MessageSendCommandDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MessageRabbitMQMapper Tests")
class MessageRabbitMQMapperTest {

    private MessageRabbitMQMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new MessageRabbitMQMapper();
    }

    @Test
    @DisplayName("Deve converter DTO RabbitMQ válido para Command de domínio")
    void shouldConvertValidRabbitMQDTOToCommand() {
        // Given
        String companyId = "550e8400-e29b-41d4-a716-446655440000";
        String xCorrelationId = "test-correlation-123";
        String recipient = "test@example.com";
        String subject = "Test Subject";
        String content = "Test Content";
        String codeUser = "user123";
        Map<String, Object> variables = Map.of("name", "John", "company", "Test Corp");

        MessageSendRabbitMQDTO rabbitMQMessage = new MessageSendRabbitMQDTO(
            companyId, xCorrelationId, MessageTypeEnum.EMAIL, recipient,
            TemplateTypeEnum.NOTIFICACAO_GERAL, subject, content, CommunicationTypeEnum.EMAIL,
            codeUser, variables
        );

        // When
        MessageSendCommandDTO command = mapper.toSendCommand(rabbitMQMessage);

        // Then
        assertNotNull(command);
        assertEquals(UUID.fromString(companyId), command.getCompanyId());
        assertEquals(CommunicationTypeEnum.EMAIL, command.getCommunicationType());
        assertEquals(recipient, command.getRecipient());
        assertEquals(subject, command.getSubject());
        assertEquals(content, command.getContent());
        assertEquals("EMAIL", command.getMessageType());
        assertEquals("NOTIFICACAO_GERAL", command.getTemplateType());
        assertEquals(codeUser, command.getCodeUser());
        assertEquals(variables, command.getVariables());
    }

    @Test
    @DisplayName("Deve converter DTO RabbitMQ com campos nulos para Command")
    void shouldConvertRabbitMQDTOWithNullFieldsToCommand() {
        // Given
        String companyId = "550e8400-e29b-41d4-a716-446655440000";
        String xCorrelationId = "test-correlation-123";
        String recipient = "test@example.com";

        MessageSendRabbitMQDTO rabbitMQMessage = new MessageSendRabbitMQDTO(
            companyId, xCorrelationId, MessageTypeEnum.EMAIL, recipient,
            TemplateTypeEnum.NOTIFICACAO_GERAL, null, null, CommunicationTypeEnum.EMAIL,
            null, null
        );

        // When
        MessageSendCommandDTO command = mapper.toSendCommand(rabbitMQMessage);

        // Then
        assertNotNull(command);
        assertEquals(UUID.fromString(companyId), command.getCompanyId());
        assertEquals(CommunicationTypeEnum.EMAIL, command.getCommunicationType());
        assertEquals(recipient, command.getRecipient());
        assertNull(command.getSubject());
        assertNull(command.getContent());
        assertEquals("EMAIL", command.getMessageType());
        assertEquals("NOTIFICACAO_GERAL", command.getTemplateType());
        assertNull(command.getCodeUser());
        assertNull(command.getVariables());
    }

    @Test
    @DisplayName("Deve lançar exceção quando DTO RabbitMQ é inválido")
    void shouldThrowExceptionWhenRabbitMQDTOIsInvalid() {
        // Given
        MessageSendRabbitMQDTO invalidMessage = new MessageSendRabbitMQDTO(
            null, null, null, null, null, null, null, null, null, null
        );

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> mapper.toSendCommand(invalidMessage)
        );

        assertTrue(exception.getMessage().contains("Mensagem RabbitMQ inválida"));
    }

    @Test
    @DisplayName("Deve converter diferentes tipos de mensagem corretamente")
    void shouldConvertDifferentMessageTypesCorrectly() {
        // Given
        String companyId = "550e8400-e29b-41d4-a716-446655440000";
        String xCorrelationId = "test-correlation-123";
        String recipient = "test@example.com";

        MessageSendRabbitMQDTO smsMessage = new MessageSendRabbitMQDTO(
            companyId, xCorrelationId, MessageTypeEnum.SMS, recipient,
            TemplateTypeEnum.AUTENTICACAO_SMS_TOKEN, "SMS Subject", "SMS Content", 
            CommunicationTypeEnum.SMS, "user123", Map.of("code", "123456")
        );

        // When
        MessageSendCommandDTO command = mapper.toSendCommand(smsMessage);

        // Then
        assertNotNull(command);
        assertEquals("SMS", command.getMessageType());
        assertEquals("AUTENTICACAO_SMS_TOKEN", command.getTemplateType());
        assertEquals(CommunicationTypeEnum.SMS, command.getCommunicationType());
    }

    @Test
    @DisplayName("Deve converter diferentes tipos de template corretamente")
    void shouldConvertDifferentTemplateTypesCorrectly() {
        // Given
        String companyId = "550e8400-e29b-41d4-a716-446655440000";
        String xCorrelationId = "test-correlation-123";
        String recipient = "test@example.com";

        MessageSendRabbitMQDTO authMessage = new MessageSendRabbitMQDTO(
            companyId, xCorrelationId, MessageTypeEnum.EMAIL, recipient,
            TemplateTypeEnum.AUTENTICACAO_EMAIL_TOKEN, "Auth Subject", "Auth Content", 
            CommunicationTypeEnum.EMAIL, "user123", Map.of("token", "abc123")
        );

        // When
        MessageSendCommandDTO command = mapper.toSendCommand(authMessage);

        // Then
        assertNotNull(command);
        assertEquals("AUTENTICACAO_EMAIL_TOKEN", command.getTemplateType());
        assertEquals(CommunicationTypeEnum.EMAIL, command.getCommunicationType());
    }

    @Test
    @DisplayName("Deve converter DTO com variáveis complexas")
    void shouldConvertDTOWithComplexVariables() {
        // Given
        String companyId = "550e8400-e29b-41d4-a716-446655440000";
        String xCorrelationId = "test-correlation-123";
        String recipient = "test@example.com";
        Map<String, Object> complexVariables = Map.of(
            "user", Map.of("name", "John", "age", 30),
            "company", "Test Corp",
            "settings", Map.of("theme", "dark", "notifications", true)
        );

        MessageSendRabbitMQDTO rabbitMQMessage = new MessageSendRabbitMQDTO(
            companyId, xCorrelationId, MessageTypeEnum.EMAIL, recipient,
            TemplateTypeEnum.NOTIFICACAO_GERAL, "Complex Subject", "Complex Content", 
            CommunicationTypeEnum.EMAIL, "user123", complexVariables
        );

        // When
        MessageSendCommandDTO command = mapper.toSendCommand(rabbitMQMessage);

        // Then
        assertNotNull(command);
        assertEquals(complexVariables, command.getVariables());
        assertEquals("Complex Subject", command.getSubject());
        assertEquals("Complex Content", command.getContent());
    }
}
