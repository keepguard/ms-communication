package com.keepguard.ms_communication.application.service.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para ProviderNotFoundException
 */
@DisplayName("ProviderNotFoundException Tests")
class ProviderNotFoundExceptionTest {

    @Test
    @DisplayName("Deve criar ProviderNotFoundException com mensagem")
    void shouldCreateProviderNotFoundExceptionWithMessage() {
        // Given
        String message = "Provider not found";

        // When
        ProviderNotFoundException exception = new ProviderNotFoundException(message);

        // Then
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("Deve criar ProviderNotFoundException com mensagem nula")
    void shouldCreateProviderNotFoundExceptionWithNullMessage() {
        // When
        ProviderNotFoundException exception = new ProviderNotFoundException(null);

        // Then
        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("Deve criar ProviderNotFoundException com mensagem vazia")
    void shouldCreateProviderNotFoundExceptionWithEmptyMessage() {
        // Given
        String message = "";

        // When
        ProviderNotFoundException exception = new ProviderNotFoundException(message);

        // Then
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("Deve criar ProviderNotFoundException com mensagem contendo caracteres especiais")
    void shouldCreateProviderNotFoundExceptionWithSpecialCharacters() {
        // Given
        String message = "Provider with ID 123-abc-456 not found!";

        // When
        ProviderNotFoundException exception = new ProviderNotFoundException(message);

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
        ProviderNotFoundException exception = new ProviderNotFoundException(message);

        // Then
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    @DisplayName("Deve permitir lançamento da exceção")
    void shouldAllowThrowingException() {
        // Given
        String message = "Test exception";

        // When & Then
        ProviderNotFoundException exception = assertThrows(ProviderNotFoundException.class, () -> {
            throw new ProviderNotFoundException(message);
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
            throw new ProviderNotFoundException(message);
        });

        assertTrue(exception instanceof ProviderNotFoundException);
        assertEquals(message, exception.getMessage());
    }
}
