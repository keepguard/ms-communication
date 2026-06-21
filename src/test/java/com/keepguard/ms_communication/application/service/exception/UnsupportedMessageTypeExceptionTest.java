package com.keepguard.ms_communication.application.service.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para UnsupportedMessageTypeException
 */
@DisplayName("UnsupportedMessageTypeException Tests")
class UnsupportedMessageTypeExceptionTest {

    @Test
    @DisplayName("Deve criar UnsupportedMessageTypeException com mensagem")
    void shouldCreateUnsupportedMessageTypeExceptionWithMessage() {
        // Given
        String message = "Message type is not supported";

        // When
        UnsupportedMessageTypeException exception = new UnsupportedMessageTypeException(message);

        // Then
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("Deve criar UnsupportedMessageTypeException com mensagem nula")
    void shouldCreateUnsupportedMessageTypeExceptionWithNullMessage() {
        // When
        UnsupportedMessageTypeException exception = new UnsupportedMessageTypeException(null);

        // Then
        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("Deve criar UnsupportedMessageTypeException com mensagem vazia")
    void shouldCreateUnsupportedMessageTypeExceptionWithEmptyMessage() {
        // Given
        String message = "";

        // When
        UnsupportedMessageTypeException exception = new UnsupportedMessageTypeException(message);

        // Then
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("Deve criar UnsupportedMessageTypeException com mensagem contendo caracteres especiais")
    void shouldCreateUnsupportedMessageTypeExceptionWithSpecialCharacters() {
        // Given
        String message = "Message type 'VOICE-MESSAGE_123' is not supported!";

        // When
        UnsupportedMessageTypeException exception = new UnsupportedMessageTypeException(message);

        // Then
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("Deve herdar de RuntimeException")
    void shouldInheritFromRuntimeException() {
        // Given
        String message = "Test message";

        // When
        UnsupportedMessageTypeException exception = new UnsupportedMessageTypeException(message);

        // Then
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    @DisplayName("Deve permitir lançamento da exceção")
    void shouldAllowThrowingException() {
        // Given
        String message = "Test exception";

        // When & Then
        UnsupportedMessageTypeException exception = assertThrows(UnsupportedMessageTypeException.class, () -> {
            throw new UnsupportedMessageTypeException(message);
        });

        assertEquals(message, exception.getMessage());
    }

    @Test
    @DisplayName("Deve permitir captura da exceção como RuntimeException")
    void shouldAllowCatchingAsRuntimeException() {
        // Given
        String message = "Test exception";

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            throw new UnsupportedMessageTypeException(message);
        });

        assertTrue(exception instanceof UnsupportedMessageTypeException);
        assertEquals(message, exception.getMessage());
    }
}
