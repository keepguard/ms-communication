package com.keepguard.ms_communication.adapters.in.messaging.rabbitmq.dto;

import com.keepguard.lib_common.communication.enums.CommunicationTypeEnum;
import com.keepguard.lib_common.communication.enums.MessageTypeEnum;
import com.keepguard.lib_common.communication.enums.TemplateTypeEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MessageSendRabbitMQDTO Tests")
class MessageSendRabbitMQDTOTest {

    @Test
    @DisplayName("Deve criar DTO válido com todos os campos obrigatórios")
    void shouldCreateValidDTOWithRequiredFields() {
        // Given
        String tenantId = "test-app";
        String xCorrelationId = "test-correlation-123";
        String recipient = "test@example.com";
        String subject = "Test Subject";
        String content = "Test Content";

        // When
        MessageSendRabbitMQDTO dto = new MessageSendRabbitMQDTO(
            tenantId, xCorrelationId, MessageTypeEnum.EMAIL, recipient,
            TemplateTypeEnum.NOTIFICACAO_GERAL, subject, content, CommunicationTypeEnum.EMAIL,
            "user123", Map.of("name", "John")
        );

        // Then
        assertNotNull(dto);
        assertEquals(tenantId, dto.tenantId());
        assertEquals(xCorrelationId, dto.xCorrelationId());
        assertEquals(MessageTypeEnum.EMAIL, dto.messageType());
        assertEquals(recipient, dto.recipient());
        assertEquals(TemplateTypeEnum.NOTIFICACAO_GERAL, dto.templateType());
        assertEquals(subject, dto.subject());
        assertEquals(content, dto.content());
        assertEquals(CommunicationTypeEnum.EMAIL, dto.communicationType());
        assertEquals("user123", dto.codeUser());
        assertTrue(dto.isValid());
    }

    @Test
    @DisplayName("Deve criar DTO válido com todos os campos")
    void shouldCreateValidDTOWithAllFields() {
        // Given
        String tenantId = "test-app";
        String xCorrelationId = "test-correlation-123";
        String recipient = "test@example.com";
        String subject = "Test Subject";
        String content = "Test Content";
        String codeUser = "user123";
        Map<String, Object> variables = Map.of("name", "John", "company", "Test Corp");

        // When
        MessageSendRabbitMQDTO dto = new MessageSendRabbitMQDTO(
            tenantId, xCorrelationId, MessageTypeEnum.EMAIL, recipient,
            TemplateTypeEnum.NOTIFICACAO_GERAL, subject, content, CommunicationTypeEnum.EMAIL,
            codeUser, variables
        );

        // Then
        assertNotNull(dto);
        assertEquals(tenantId, dto.tenantId());
        assertEquals(xCorrelationId, dto.xCorrelationId());
        assertEquals(MessageTypeEnum.EMAIL, dto.messageType());
        assertEquals(recipient, dto.recipient());
        assertEquals(TemplateTypeEnum.NOTIFICACAO_GERAL, dto.templateType());
        assertEquals(subject, dto.subject());
        assertEquals(content, dto.content());
        assertEquals(CommunicationTypeEnum.EMAIL, dto.communicationType());
        assertEquals(codeUser, dto.codeUser());
        assertEquals(variables, dto.variables());
        assertTrue(dto.isValid());
    }

    @Test
    @DisplayName("Deve retornar false para isValid quando campos obrigatórios estão nulos")
    void shouldReturnFalseForIsValidWhenRequiredFieldsAreNull() {
        // Given
        MessageSendRabbitMQDTO dto = new MessageSendRabbitMQDTO(
            null, null, null, null, null, null, null, null, null, null
        );

        // When & Then
        assertFalse(dto.isValid());
    }

    @Test
    @DisplayName("Deve retornar false para isValid quando campos obrigatórios estão vazios")
    void shouldReturnFalseForIsValidWhenRequiredFieldsAreEmpty() {
        // Given
        MessageSendRabbitMQDTO dto = new MessageSendRabbitMQDTO(
            "", "", MessageTypeEnum.EMAIL, "", TemplateTypeEnum.NOTIFICACAO_GERAL, 
            "subject", "content", CommunicationTypeEnum.EMAIL, "user", null
        );

        // When & Then
        assertFalse(dto.isValid());
    }

    @Test
    @DisplayName("Deve gerar string de log válida")
    void shouldGenerateValidLogString() {
        // Given
        String tenantId = "test-app";
        String xCorrelationId = "test-correlation-123";
        String recipient = "test@example.com";
        String subject = "Test Subject";
        String content = "Test Content";
        
        MessageSendRabbitMQDTO dto = new MessageSendRabbitMQDTO(
            tenantId, xCorrelationId, MessageTypeEnum.EMAIL, recipient,
            TemplateTypeEnum.NOTIFICACAO_GERAL, subject, content, CommunicationTypeEnum.EMAIL,
            "user123", Map.of("name", "John")
        );

        // When
        String logString = dto.toLogString();

        // Then
        assertNotNull(logString);
        assertTrue(logString.contains(tenantId));
        assertTrue(logString.contains(xCorrelationId));
        assertTrue(logString.contains(recipient));
        assertTrue(logString.contains("user123"));
    }
}
