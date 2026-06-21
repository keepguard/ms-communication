package com.keepguard.ms_communication.application.service.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para MessageSendException
 */
@DisplayName("MessageSendException Tests")
class MessageSendExceptionTest {

    @Test
    @DisplayName("Deve criar MessageSendException com mensagem")
    void shouldCreateMessageSendExceptionWithMessage() {
        // Given
        String message = "Failed to send message";

        // When
        MessageSendException exception = new MessageSendException(message);

        // Then
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("Deve criar MessageSendException com mensagem e causa")
    void shouldCreateMessageSendExceptionWithMessageAndCause() {
        // Given
        String message = "Failed to send message";
        RuntimeException cause = new RuntimeException("Connection timeout");

        // When
        MessageSendException exception = new MessageSendException(message, cause);

        // Then
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    @DisplayName("Deve criar MessageSendException com mensagem nula")
    void shouldCreateMessageSendExceptionWithNullMessage() {
        // When
        MessageSendException exception = new MessageSendException(null);

        // Then
        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("Deve criar MessageSendException com mensagem nula e causa")
    void shouldCreateMessageSendExceptionWithNullMessageAndCause() {
        // Given
        RuntimeException cause = new RuntimeException("Test cause");

        // When
        MessageSendException exception = new MessageSendException(null, cause);

        // Then
        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    @DisplayName("Deve criar MessageSendException com mensagem vazia")
    void shouldCreateMessageSendExceptionWithEmptyMessage() {
        // Given
        String message = "";

        // When
        MessageSendException exception = new MessageSendException(message);

        // Then
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("Deve criar MessageSendException com mensagem e causa nula")
    void shouldCreateMessageSendExceptionWithMessageAndNullCause() {
        // Given
        String message = "Test message";

        // When
        MessageSendException exception = new MessageSendException(message, null);

        // Then
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("Deve criar MessageSendException com mensagem contendo caracteres especiais")
    void shouldCreateMessageSendExceptionWithSpecialCharacters() {
        // Given
        String message = "Failed to send message to user@example.com (ID: 123-abc-456)!";

        // When
        MessageSendException exception = new MessageSendException(message);

        // Then
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("Deve criar MessageSendException com diferentes tipos de causa")
    void shouldCreateMessageSendExceptionWithDifferentCauseTypes() {
        // Given
        String message = "Test message";
        
        // Test with RuntimeException
        RuntimeException runtimeCause = new RuntimeException("Runtime error");
        MessageSendException exception1 = new MessageSendException(message, runtimeCause);
        assertEquals(runtimeCause, exception1.getCause());
        
        // Test with IllegalArgumentException
        IllegalArgumentException illegalArgCause = new IllegalArgumentException("Invalid argument");
        MessageSendException exception2 = new MessageSendException(message, illegalArgCause);
        assertEquals(illegalArgCause, exception2.getCause());
        
        // Test with Exception
        Exception generalCause = new Exception("General error");
        MessageSendException exception3 = new MessageSendException(message, generalCause);
        assertEquals(generalCause, exception3.getCause());
    }

    @Test
    @DisplayName("Deve herdar de RuntimeException")
    void shouldInheritFromRuntimeException() {
        // Given
        String message = "Test message";

        // When
        MessageSendException exception = new MessageSendException(message);

        // Then
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    @DisplayName("Deve permitir lançamento da exceção com mensagem")
    void shouldAllowThrowingExceptionWithMessage() {
        // Given
        String message = "Test exception";

        // When & Then
        MessageSendException exception = assertThrows(MessageSendException.class, () -> {
            throw new MessageSendException(message);
        });

        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("Deve permitir lançamento da exceção com mensagem e causa")
    void shouldAllowThrowingExceptionWithMessageAndCause() {
        // Given
        String message = "Test exception";
        RuntimeException cause = new RuntimeException("Test cause");

        // When & Then
        MessageSendException exception = assertThrows(MessageSendException.class, () -> {
            throw new MessageSendException(message, cause);
        });

        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    @DisplayName("Deve permitir captura da exceção como RuntimeException")
    void shouldAllowCatchingAsRuntimeException() {
        // Given
        String message = "Test exception";

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            throw new MessageSendException(message);
        });

        assertTrue(exception instanceof MessageSendException);
        assertEquals(message, exception.getMessage());
    }

    @Test
    @DisplayName("Deve permitir captura da exceção com causa como RuntimeException")
    void shouldAllowCatchingWithCauseAsRuntimeException() {
        // Given
        String message = "Test exception";
        RuntimeException originalCause = new RuntimeException("Original cause");

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            throw new MessageSendException(message, originalCause);
        });

        assertTrue(exception instanceof MessageSendException);
        assertEquals(message, exception.getMessage());
        assertEquals(originalCause, exception.getCause());
    }
}
