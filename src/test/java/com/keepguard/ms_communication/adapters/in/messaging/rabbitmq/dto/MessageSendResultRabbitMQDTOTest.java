package com.keepguard.ms_communication.adapters.in.messaging.rabbitmq.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MessageSendResultRabbitMQDTO Tests")
class MessageSendResultRabbitMQDTOTest {

    @Test
    @DisplayName("Deve criar DTO de resultado válido com sucesso")
    void shouldCreateValidResultDTOWithSuccess() {
        // Given
        String xCorrelationId = "test-correlation-123";
        Boolean success = true;
        String errorCode = null;
        String errorMessage = null;
        LocalDateTime processedAt = LocalDateTime.now();

        // When
        MessageSendResultRabbitMQDTO result = new MessageSendResultRabbitMQDTO(
            xCorrelationId, success, errorCode, errorMessage, processedAt
        );

        // Then
        assertNotNull(result);
        assertEquals(xCorrelationId, result.xCorrelationId());
        assertEquals(success, result.success());
        assertNull(result.errorCode());
        assertNull(result.errorMessage());
        assertEquals(processedAt, result.processedAt());
        assertTrue(result.isSuccess());
        assertFalse(result.hasError());
    }

    @Test
    @DisplayName("Deve criar DTO de resultado com erro")
    void shouldCreateResultDTOWithError() {
        // Given
        String xCorrelationId = "test-correlation-123";
        Boolean success = false;
        String errorCode = "VALIDATION_ERROR";
        String errorMessage = "Mensagem inválida";
        LocalDateTime processedAt = LocalDateTime.now();

        // When
        MessageSendResultRabbitMQDTO result = new MessageSendResultRabbitMQDTO(
            xCorrelationId, success, errorCode, errorMessage, processedAt
        );

        // Then
        assertNotNull(result);
        assertEquals(xCorrelationId, result.xCorrelationId());
        assertEquals(success, result.success());
        assertEquals(errorCode, result.errorCode());
        assertEquals(errorMessage, result.errorMessage());
        assertEquals(processedAt, result.processedAt());
        assertFalse(result.isSuccess());
        assertTrue(result.hasError());
    }

    @Test
    @DisplayName("Deve criar DTO de resultado com erro apenas no código")
    void shouldCreateResultDTOWithErrorCodeOnly() {
        // Given
        String xCorrelationId = "test-correlation-123";
        Boolean success = false;
        String errorCode = "PROCESSING_ERROR";
        String errorMessage = null;
        LocalDateTime processedAt = LocalDateTime.now();

        // When
        MessageSendResultRabbitMQDTO result = new MessageSendResultRabbitMQDTO(
            xCorrelationId, success, errorCode, errorMessage, processedAt
        );

        // Then
        assertNotNull(result);
        assertEquals(xCorrelationId, result.xCorrelationId());
        assertEquals(success, result.success());
        assertEquals(errorCode, result.errorCode());
        assertNull(result.errorMessage());
        assertEquals(processedAt, result.processedAt());
        assertFalse(result.isSuccess());
        assertTrue(result.hasError());
    }

    @Test
    @DisplayName("Deve criar DTO de resultado com erro apenas na mensagem")
    void shouldCreateResultDTOWithErrorMessageOnly() {
        // Given
        String xCorrelationId = "test-correlation-123";
        Boolean success = false;
        String errorCode = null;
        String errorMessage = "Erro interno do servidor";
        LocalDateTime processedAt = LocalDateTime.now();

        // When
        MessageSendResultRabbitMQDTO result = new MessageSendResultRabbitMQDTO(
            xCorrelationId, success, errorCode, errorMessage, processedAt
        );

        // Then
        assertNotNull(result);
        assertEquals(xCorrelationId, result.xCorrelationId());
        assertEquals(success, result.success());
        assertNull(result.errorCode());
        assertEquals(errorMessage, result.errorMessage());
        assertEquals(processedAt, result.processedAt());
        assertFalse(result.isSuccess());
        assertTrue(result.hasError());
    }

    @Test
    @DisplayName("Deve criar DTO de resultado sem erro quando sucesso é true")
    void shouldCreateResultDTOWithoutErrorWhenSuccessIsTrue() {
        // Given
        String xCorrelationId = "test-correlation-123";
        Boolean success = true;
        String errorCode = "SOME_ERROR_CODE";
        String errorMessage = "Some error message";
        LocalDateTime processedAt = LocalDateTime.now();

        // When
        MessageSendResultRabbitMQDTO result = new MessageSendResultRabbitMQDTO(
            xCorrelationId, success, errorCode, errorMessage, processedAt
        );

        // Then
        assertNotNull(result);
        assertEquals(xCorrelationId, result.xCorrelationId());
        assertEquals(success, result.success());
        assertEquals(errorCode, result.errorCode());
        assertEquals(errorMessage, result.errorMessage());
        assertEquals(processedAt, result.processedAt());
        assertTrue(result.isSuccess());
        assertFalse(result.hasError());
    }

    @Test
    @DisplayName("Deve gerar string de log válida para resultado de sucesso")
    void shouldGenerateValidLogStringForSuccessResult() {
        // Given
        String xCorrelationId = "test-correlation-123";
        Boolean success = true;
        String errorCode = null;
        String errorMessage = null;
        LocalDateTime processedAt = LocalDateTime.now();

        MessageSendResultRabbitMQDTO result = new MessageSendResultRabbitMQDTO(
            xCorrelationId, success, errorCode, errorMessage, processedAt
        );

        // When
        String logString = result.toLogString();

        // Then
        assertNotNull(logString);
        assertTrue(logString.contains(xCorrelationId));
        assertTrue(logString.contains("true"));
        assertTrue(logString.contains("null"));
        assertTrue(logString.contains(processedAt.toString()));
    }

    @Test
    @DisplayName("Deve gerar string de log válida para resultado com erro")
    void shouldGenerateValidLogStringForErrorResult() {
        // Given
        String xCorrelationId = "test-correlation-123";
        Boolean success = false;
        String errorCode = "VALIDATION_ERROR";
        String errorMessage = "Mensagem inválida";
        LocalDateTime processedAt = LocalDateTime.now();

        MessageSendResultRabbitMQDTO result = new MessageSendResultRabbitMQDTO(
            xCorrelationId, success, errorCode, errorMessage, processedAt
        );

        // When
        String logString = result.toLogString();

        // Then
        assertNotNull(logString);
        assertTrue(logString.contains(xCorrelationId));
        assertTrue(logString.contains("false"));
        assertTrue(logString.contains(errorCode));
        assertTrue(logString.contains(errorMessage));
        assertTrue(logString.contains(processedAt.toString()));
    }

    @Test
    @DisplayName("Deve verificar isSuccess corretamente para diferentes valores")
    void shouldCheckIsSuccessCorrectlyForDifferentValues() {
        // Given & When & Then
        MessageSendResultRabbitMQDTO successResult = new MessageSendResultRabbitMQDTO(
            "test-123", true, null, null, LocalDateTime.now()
        );
        assertTrue(successResult.isSuccess());

        MessageSendResultRabbitMQDTO failureResult = new MessageSendResultRabbitMQDTO(
            "test-123", false, null, null, LocalDateTime.now()
        );
        assertFalse(failureResult.isSuccess());

        MessageSendResultRabbitMQDTO nullSuccessResult = new MessageSendResultRabbitMQDTO(
            "test-123", null, null, null, LocalDateTime.now()
        );
        assertFalse(nullSuccessResult.isSuccess());
    }

    @Test
    @DisplayName("Deve verificar hasError corretamente para diferentes cenários")
    void shouldCheckHasErrorCorrectlyForDifferentScenarios() {
        // Given & When & Then
        // Sucesso sem erro
        MessageSendResultRabbitMQDTO successResult = new MessageSendResultRabbitMQDTO(
            "test-123", true, null, null, LocalDateTime.now()
        );
        assertFalse(successResult.hasError());

        // Falha com código de erro
        MessageSendResultRabbitMQDTO errorCodeResult = new MessageSendResultRabbitMQDTO(
            "test-123", false, "ERROR_CODE", null, LocalDateTime.now()
        );
        assertTrue(errorCodeResult.hasError());

        // Falha com mensagem de erro
        MessageSendResultRabbitMQDTO errorMessageResult = new MessageSendResultRabbitMQDTO(
            "test-123", false, null, "Error message", LocalDateTime.now()
        );
        assertTrue(errorMessageResult.hasError());

        // Falha com ambos
        MessageSendResultRabbitMQDTO bothErrorResult = new MessageSendResultRabbitMQDTO(
            "test-123", false, "ERROR_CODE", "Error message", LocalDateTime.now()
        );
        assertTrue(bothErrorResult.hasError());

        // Sucesso com código de erro (não deve ser considerado erro)
        MessageSendResultRabbitMQDTO successWithErrorCodeResult = new MessageSendResultRabbitMQDTO(
            "test-123", true, "ERROR_CODE", "Error message", LocalDateTime.now()
        );
        assertFalse(successWithErrorCodeResult.hasError());
    }

    @Test
    @DisplayName("Deve criar DTO com valores extremos")
    void shouldCreateDTOWithExtremeValues() {
        // Given
        String xCorrelationId = "a".repeat(100); // String máxima
        Boolean success = false;
        String errorCode = "E".repeat(50);
        String errorMessage = "M".repeat(1000);
        LocalDateTime processedAt = LocalDateTime.now();

        // When
        MessageSendResultRabbitMQDTO result = new MessageSendResultRabbitMQDTO(
            xCorrelationId, success, errorCode, errorMessage, processedAt
        );

        // Then
        assertNotNull(result);
        assertEquals(xCorrelationId, result.xCorrelationId());
        assertEquals(success, result.success());
        assertEquals(errorCode, result.errorCode());
        assertEquals(errorMessage, result.errorMessage());
        assertEquals(processedAt, result.processedAt());
        assertFalse(result.isSuccess());
        assertTrue(result.hasError());
    }
}
