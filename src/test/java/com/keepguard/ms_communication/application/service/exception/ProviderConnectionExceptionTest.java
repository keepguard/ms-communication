package com.keepguard.ms_communication.application.service.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para ProviderConnectionException
 */
@DisplayName("ProviderConnectionException Tests")
class ProviderConnectionExceptionTest {

    @Test
    @DisplayName("Deve criar ProviderConnectionException com mensagem")
    void shouldCreateProviderConnectionExceptionWithMessage() {
        // Given
        String message = "Failed to connect to provider";

        // When
        ProviderConnectionException exception = new ProviderConnectionException(message);

        // Then
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("Deve criar ProviderConnectionException com mensagem e causa")
    void shouldCreateProviderConnectionExceptionWithMessageAndCause() {
        // Given
        String message = "Failed to connect to provider";
        RuntimeException cause = new RuntimeException("Connection timeout");

        // When
        ProviderConnectionException exception = new ProviderConnectionException(message, cause);

        // Then
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    @DisplayName("Deve criar ProviderConnectionException com mensagem nula")
    void shouldCreateProviderConnectionExceptionWithNullMessage() {
        // When
        ProviderConnectionException exception = new ProviderConnectionException(null);

        // Then
        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("Deve criar ProviderConnectionException com mensagem nula e causa")
    void shouldCreateProviderConnectionExceptionWithNullMessageAndCause() {
        // Given
        RuntimeException cause = new RuntimeException("Test cause");

        // When
        ProviderConnectionException exception = new ProviderConnectionException(null, cause);

        // Then
        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    @DisplayName("Deve criar ProviderConnectionException com mensagem vazia")
    void shouldCreateProviderConnectionExceptionWithEmptyMessage() {
        // Given
        String message = "";

        // When
        ProviderConnectionException exception = new ProviderConnectionException(message);

        // Then
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("Deve criar ProviderConnectionException com mensagem e causa nula")
    void shouldCreateProviderConnectionExceptionWithMessageAndNullCause() {
        // Given
        String message = "Test message";

        // When
        ProviderConnectionException exception = new ProviderConnectionException(message, null);

        // Then
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("Deve criar ProviderConnectionException com mensagem contendo caracteres especiais")
    void shouldCreateProviderConnectionExceptionWithSpecialCharacters() {
        // Given
        String message = "Failed to connect to provider 'N8N-API_123' at https://api.example.com!";

        // When
        ProviderConnectionException exception = new ProviderConnectionException(message);

        // Then
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("Deve criar ProviderConnectionException com diferentes tipos de causa")
    void shouldCreateProviderConnectionExceptionWithDifferentCauseTypes() {
        // Given
        String message = "Test message";
        
        // Test with RuntimeException
        RuntimeException runtimeCause = new RuntimeException("Runtime error");
        ProviderConnectionException exception1 = new ProviderConnectionException(message, runtimeCause);
        assertEquals(runtimeCause, exception1.getCause());
        
        // Test with IllegalArgumentException
        IllegalArgumentException illegalArgCause = new IllegalArgumentException("Invalid argument");
        ProviderConnectionException exception2 = new ProviderConnectionException(message, illegalArgCause);
        assertEquals(illegalArgCause, exception2.getCause());
        
        // Test with Exception
        Exception generalCause = new Exception("General error");
        ProviderConnectionException exception3 = new ProviderConnectionException(message, generalCause);
        assertEquals(generalCause, exception3.getCause());
    }

    @Test
    @DisplayName("Deve herdar de RuntimeException")
    void shouldInheritFromRuntimeException() {
        // Given
        String message = "Test message";

        // When
        ProviderConnectionException exception = new ProviderConnectionException(message);

        // Then
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    @DisplayName("Deve permitir lançamento da exceção com mensagem")
    void shouldAllowThrowingExceptionWithMessage() {
        // Given
        String message = "Test exception";

        // When & Then
        ProviderConnectionException exception = assertThrows(ProviderConnectionException.class, () -> {
            throw new ProviderConnectionException(message);
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
        ProviderConnectionException exception = assertThrows(ProviderConnectionException.class, () -> {
            throw new ProviderConnectionException(message, cause);
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
            throw new ProviderConnectionException(message);
        });

        assertTrue(exception instanceof ProviderConnectionException);
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
            throw new ProviderConnectionException(message, originalCause);
        });

        assertTrue(exception instanceof ProviderConnectionException);
        assertEquals(message, exception.getMessage());
        assertEquals(originalCause, exception.getCause());
    }
}
