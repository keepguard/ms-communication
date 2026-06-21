package com.keepguard.ms_communication.application.service.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para RequiredFieldException
 */
@DisplayName("RequiredFieldException Tests")
class RequiredFieldExceptionTest {

    @Test
    @DisplayName("Deve criar RequiredFieldException com mensagem")
    void shouldCreateRequiredFieldExceptionWithMessage() {
        // Given
        String message = "Required field is missing";

        // When
        RequiredFieldException exception = new RequiredFieldException(message);

        // Then
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("Deve criar RequiredFieldException com mensagem nula")
    void shouldCreateRequiredFieldExceptionWithNullMessage() {
        // When
        RequiredFieldException exception = new RequiredFieldException(null);

        // Then
        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("Deve criar RequiredFieldException com mensagem vazia")
    void shouldCreateRequiredFieldExceptionWithEmptyMessage() {
        // Given
        String message = "";

        // When
        RequiredFieldException exception = new RequiredFieldException(message);

        // Then
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("Deve criar RequiredFieldException com mensagem contendo caracteres especiais")
    void shouldCreateRequiredFieldExceptionWithSpecialCharacters() {
        // Given
        String message = "Required field 'user-email_123' is missing!";

        // When
        RequiredFieldException exception = new RequiredFieldException(message);

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
        RequiredFieldException exception = new RequiredFieldException(message);

        // Then
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    @DisplayName("Deve permitir lançamento da exceção")
    void shouldAllowThrowingException() {
        // Given
        String message = "Test exception";

        // When & Then
        RequiredFieldException exception = assertThrows(RequiredFieldException.class, () -> {
            throw new RequiredFieldException(message);
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
            throw new RequiredFieldException(message);
        });

        assertTrue(exception instanceof RequiredFieldException);
        assertEquals(message, exception.getMessage());
    }
}
