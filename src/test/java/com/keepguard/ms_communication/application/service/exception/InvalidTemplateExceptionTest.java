package com.keepguard.ms_communication.application.service.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para InvalidTemplateException
 */
@DisplayName("InvalidTemplateException Tests")
class InvalidTemplateExceptionTest {

    @Test
    @DisplayName("Deve criar InvalidTemplateException com mensagem")
    void shouldCreateInvalidTemplateExceptionWithMessage() {
        // Given
        String message = "Template is invalid";

        // When
        InvalidTemplateException exception = new InvalidTemplateException(message);

        // Then
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("Deve criar InvalidTemplateException com mensagem nula")
    void shouldCreateInvalidTemplateExceptionWithNullMessage() {
        // When
        InvalidTemplateException exception = new InvalidTemplateException(null);

        // Then
        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("Deve criar InvalidTemplateException com mensagem vazia")
    void shouldCreateInvalidTemplateExceptionWithEmptyMessage() {
        // Given
        String message = "";

        // When
        InvalidTemplateException exception = new InvalidTemplateException(message);

        // Then
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("Deve criar InvalidTemplateException com mensagem contendo caracteres especiais")
    void shouldCreateInvalidTemplateExceptionWithSpecialCharacters() {
        // Given
        String message = "Template 'Test-Template_123' is invalid: missing required variables!";

        // When
        InvalidTemplateException exception = new InvalidTemplateException(message);

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
        InvalidTemplateException exception = new InvalidTemplateException(message);

        // Then
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    @DisplayName("Deve permitir lançamento da exceção")
    void shouldAllowThrowingException() {
        // Given
        String message = "Test exception";

        // When & Then
        InvalidTemplateException exception = assertThrows(InvalidTemplateException.class, () -> {
            throw new InvalidTemplateException(message);
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
            throw new InvalidTemplateException(message);
        });

        assertTrue(exception instanceof InvalidTemplateException);
        assertEquals(message, exception.getMessage());
    }
}
