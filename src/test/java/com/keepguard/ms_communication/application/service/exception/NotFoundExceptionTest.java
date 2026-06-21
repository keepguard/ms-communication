package com.keepguard.ms_communication.application.service.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para NotFoundException
 */
@DisplayName("NotFoundException Tests")
class NotFoundExceptionTest {

    @Test
    @DisplayName("Deve criar NotFoundException com mensagem")
    void shouldCreateNotFoundExceptionWithMessage() {
        // Given
        String message = "Resource not found";

        // When
        NotFoundException exception = new NotFoundException(message);

        // Then
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("Deve criar NotFoundException com mensagem nula")
    void shouldCreateNotFoundExceptionWithNullMessage() {
        // When
        NotFoundException exception = new NotFoundException(null);

        // Then
        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("Deve criar NotFoundException com mensagem vazia")
    void shouldCreateNotFoundExceptionWithEmptyMessage() {
        // Given
        String message = "";

        // When
        NotFoundException exception = new NotFoundException(message);

        // Then
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("Deve criar NotFoundException com mensagem contendo caracteres especiais")
    void shouldCreateNotFoundExceptionWithSpecialCharacters() {
        // Given
        String message = "Resource with ID 123-abc-456 not found!";

        // When
        NotFoundException exception = new NotFoundException(message);

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
        NotFoundException exception = new NotFoundException(message);

        // Then
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    @DisplayName("Deve permitir lançamento da exceção")
    void shouldAllowThrowingException() {
        // Given
        String message = "Test exception";

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            throw new NotFoundException(message);
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
            throw new NotFoundException(message);
        });

        assertTrue(exception instanceof NotFoundException);
        assertEquals(message, exception.getMessage());
    }
}
