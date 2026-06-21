package com.keepguard.ms_communication.application.service.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para AlreadyExistsException
 */
@DisplayName("AlreadyExistsException Tests")
class AlreadyExistsExceptionTest {

    @Test
    @DisplayName("Deve criar AlreadyExistsException com mensagem")
    void shouldCreateAlreadyExistsExceptionWithMessage() {
        // Given
        String message = "Resource already exists";

        // When
        AlreadyExistsException exception = new AlreadyExistsException(message);

        // Then
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("Deve criar AlreadyExistsException com mensagem nula")
    void shouldCreateAlreadyExistsExceptionWithNullMessage() {
        // When
        AlreadyExistsException exception = new AlreadyExistsException(null);

        // Then
        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("Deve criar AlreadyExistsException com mensagem vazia")
    void shouldCreateAlreadyExistsExceptionWithEmptyMessage() {
        // Given
        String message = "";

        // When
        AlreadyExistsException exception = new AlreadyExistsException(message);

        // Then
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("Deve criar AlreadyExistsException com mensagem contendo caracteres especiais")
    void shouldCreateAlreadyExistsExceptionWithSpecialCharacters() {
        // Given
        String message = "Provider with name 'Test-Provider_123' already exists!";

        // When
        AlreadyExistsException exception = new AlreadyExistsException(message);

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
        AlreadyExistsException exception = new AlreadyExistsException(message);

        // Then
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    @DisplayName("Deve permitir lançamento da exceção")
    void shouldAllowThrowingException() {
        // Given
        String message = "Test exception";

        // When & Then
        AlreadyExistsException exception = assertThrows(AlreadyExistsException.class, () -> {
            throw new AlreadyExistsException(message);
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
            throw new AlreadyExistsException(message);
        });

        assertTrue(exception instanceof AlreadyExistsException);
        assertEquals(message, exception.getMessage());
    }
}
