package com.keepguard.ms_communication.infrastructure.rest;

import com.keepguard.ms_communication.application.service.exception.AlreadyExistsException;
import com.keepguard.ms_communication.application.service.exception.MessageSendException;
import com.keepguard.ms_communication.application.service.exception.NotFoundException;
import com.keepguard.ms_communication.application.service.exception.ProviderConnectionException;
import com.keepguard.ms_communication.application.service.exception.ProviderNotFoundException;
import com.keepguard.ms_communication.application.service.exception.RequiredFieldException;
import com.keepguard.ms_communication.application.service.exception.UnsupportedMessageTypeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("GlobalExceptionHandler Tests")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;
    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
        webRequest = mock(WebRequest.class);
        when(webRequest.getDescription(false)).thenReturn("uri=/api/test");
    }

    @Test
    @DisplayName("Should handle NotFoundException correctly")
    void shouldHandleNotFoundException() {
        // Given
        NotFoundException exception = new NotFoundException("Resource not found");
        String expectedTimestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        // When
        ResponseEntity<ProblemDetail> response = globalExceptionHandler.handleNotFoundException(exception, webRequest);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        
        ProblemDetail problemDetail = response.getBody();
        assertNotNull(problemDetail);
        assertEquals(HttpStatus.NOT_FOUND.value(), problemDetail.getStatus());
        assertEquals("Resource not found", problemDetail.getDetail());
        assertEquals("Recurso não encontrado", problemDetail.getTitle());
        assertEquals(URI.create("https://keepguard.com/problems/not-found"), problemDetail.getType());
        assertEquals("/api/test", problemDetail.getProperties().get("path"));
        assertNotNull(problemDetail.getProperties().get("timestamp"));
    }

    @Test
    @DisplayName("Should handle AlreadyExistsException correctly")
    void shouldHandleAlreadyExistsException() {
        // Given
        AlreadyExistsException exception = new AlreadyExistsException("Resource already exists");
        String expectedTimestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        // When
        ResponseEntity<ProblemDetail> response = globalExceptionHandler.handleAlreadyExistsException(exception, webRequest);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        
        ProblemDetail problemDetail = response.getBody();
        assertNotNull(problemDetail);
        assertEquals(HttpStatus.CONFLICT.value(), problemDetail.getStatus());
        assertEquals("Resource already exists", problemDetail.getDetail());
        assertEquals("Recurso já existe", problemDetail.getTitle());
        assertEquals(URI.create("https://keepguard.com/problems/already-exists"), problemDetail.getType());
        assertEquals("/api/test", problemDetail.getProperties().get("path"));
        assertNotNull(problemDetail.getProperties().get("timestamp"));
    }

    @Test
    @DisplayName("Should handle RequiredFieldException correctly")
    void shouldHandleRequiredFieldException() {
        // Given
        RequiredFieldException exception = new RequiredFieldException("Required field is missing");
        String expectedTimestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        // When
        ResponseEntity<ProblemDetail> response = globalExceptionHandler.handleRequiredFieldException(exception, webRequest);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        
        ProblemDetail problemDetail = response.getBody();
        assertNotNull(problemDetail);
        assertEquals(HttpStatus.BAD_REQUEST.value(), problemDetail.getStatus());
        assertEquals("Required field is missing", problemDetail.getDetail());
        assertEquals("Campo obrigatório não informado", problemDetail.getTitle());
        assertEquals(URI.create("https://keepguard.com/problems/required-field"), problemDetail.getType());
        assertEquals("/api/test", problemDetail.getProperties().get("path"));
        assertNotNull(problemDetail.getProperties().get("timestamp"));
    }

    @Test
    @DisplayName("Should handle UnsupportedMessageTypeException correctly")
    void shouldHandleUnsupportedMessageTypeException() {
        // Given
        UnsupportedMessageTypeException exception = new UnsupportedMessageTypeException("Unsupported message type");
        String expectedTimestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        // When
        ResponseEntity<ProblemDetail> response = globalExceptionHandler.handleUnsupportedMessageTypeException(exception, webRequest);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        
        ProblemDetail problemDetail = response.getBody();
        assertNotNull(problemDetail);
        assertEquals(HttpStatus.BAD_REQUEST.value(), problemDetail.getStatus());
        assertEquals("Unsupported message type", problemDetail.getDetail());
        assertEquals("Tipo de mensagem não suportado", problemDetail.getTitle());
        assertEquals(URI.create("https://keepguard.com/problems/unsupported-message-type"), problemDetail.getType());
        assertEquals("/api/test", problemDetail.getProperties().get("path"));
        assertNotNull(problemDetail.getProperties().get("timestamp"));
    }

    @Test
    @DisplayName("Should handle ProviderNotFoundException correctly")
    void shouldHandleProviderNotFoundException() {
        // Given
        ProviderNotFoundException exception = new ProviderNotFoundException("Provider not found");
        String expectedTimestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        // When
        ResponseEntity<ProblemDetail> response = globalExceptionHandler.handleProviderNotFoundException(exception, webRequest);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        
        ProblemDetail problemDetail = response.getBody();
        assertNotNull(problemDetail);
        assertEquals(HttpStatus.NOT_FOUND.value(), problemDetail.getStatus());
        assertEquals("Provider not found", problemDetail.getDetail());
        assertEquals("Provedor não encontrado", problemDetail.getTitle());
        assertEquals(URI.create("https://keepguard.com/problems/provider-not-found"), problemDetail.getType());
        assertEquals("/api/test", problemDetail.getProperties().get("path"));
        assertNotNull(problemDetail.getProperties().get("timestamp"));
    }

    @Test
    @DisplayName("Should handle MessageSendException correctly")
    void shouldHandleMessageSendException() {
        // Given
        MessageSendException exception = new MessageSendException("Failed to send message");
        String expectedTimestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        // When
        ResponseEntity<ProblemDetail> response = globalExceptionHandler.handleMessageSendException(exception, webRequest);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        
        ProblemDetail problemDetail = response.getBody();
        assertNotNull(problemDetail);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), problemDetail.getStatus());
        assertEquals("Failed to send message", problemDetail.getDetail());
        assertEquals("Erro ao enviar mensagem", problemDetail.getTitle());
        assertEquals(URI.create("https://keepguard.com/problems/message-send-error"), problemDetail.getType());
        assertEquals("/api/test", problemDetail.getProperties().get("path"));
        assertNotNull(problemDetail.getProperties().get("timestamp"));
    }

    @Test
    @DisplayName("Should handle ProviderConnectionException correctly")
    void shouldHandleProviderConnectionException() {
        // Given
        ProviderConnectionException exception = new ProviderConnectionException("Connection failed");
        String expectedTimestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        // When
        ResponseEntity<ProblemDetail> response = globalExceptionHandler.handleProviderConnectionException(exception, webRequest);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        
        ProblemDetail problemDetail = response.getBody();
        assertNotNull(problemDetail);
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE.value(), problemDetail.getStatus());
        assertEquals("Connection failed", problemDetail.getDetail());
        assertEquals("Erro de conexão com provedor", problemDetail.getTitle());
        assertEquals(URI.create("https://keepguard.com/problems/provider-connection-error"), problemDetail.getType());
        assertEquals("/api/test", problemDetail.getProperties().get("path"));
        assertNotNull(problemDetail.getProperties().get("timestamp"));
    }

    @Test
    @DisplayName("Should handle MethodArgumentNotValidException correctly")
    void shouldHandleValidationException() {
        // Given
        BindingResult bindingResult = mock(BindingResult.class);
        List<FieldError> fieldErrors = new ArrayList<>();
        fieldErrors.add(new FieldError("testObject", "field1", "Field 1 is required"));
        fieldErrors.add(new FieldError("testObject", "field2", "Field 2 is invalid"));
        
        when(bindingResult.getAllErrors()).thenReturn(new ArrayList<>(fieldErrors));
        
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(exception.getMessage()).thenReturn("Validation failed");

        // When
        ResponseEntity<ProblemDetail> response = globalExceptionHandler.handleValidationException(exception, webRequest);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        
        ProblemDetail problemDetail = response.getBody();
        assertNotNull(problemDetail);
        assertEquals(HttpStatus.BAD_REQUEST.value(), problemDetail.getStatus());
        assertEquals("Erro de validação", problemDetail.getDetail());
        assertEquals("Erro de validação", problemDetail.getTitle());
        assertEquals(URI.create("https://keepguard.com/problems/validation-error"), problemDetail.getType());
        assertEquals("/api/test", problemDetail.getProperties().get("path"));
        assertNotNull(problemDetail.getProperties().get("timestamp"));
        
        @SuppressWarnings("unchecked")
        Map<String, String> errors = (Map<String, String>) problemDetail.getProperties().get("errors");
        assertNotNull(errors);
        assertEquals(2, errors.size());
        assertEquals("Field 1 is required", errors.get("field1"));
        assertEquals("Field 2 is invalid", errors.get("field2"));
    }

    @Test
    @DisplayName("Should handle generic Exception correctly")
    void shouldHandleGenericException() {
        // Given
        Exception exception = new RuntimeException("Unexpected error");
        String expectedTimestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        // When
        ResponseEntity<ProblemDetail> response = globalExceptionHandler.handleGenericException(exception, webRequest);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        
        ProblemDetail problemDetail = response.getBody();
        assertNotNull(problemDetail);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), problemDetail.getStatus());
        assertEquals("Erro interno do servidor", problemDetail.getDetail());
        assertEquals("Erro interno do servidor", problemDetail.getTitle());
        assertEquals(URI.create("https://keepguard.com/problems/internal-server-error"), problemDetail.getType());
        assertEquals("/api/test", problemDetail.getProperties().get("path"));
        assertNotNull(problemDetail.getProperties().get("timestamp"));
    }

    @Test
    @DisplayName("Should handle validation exception with no field errors")
    void shouldHandleValidationExceptionWithNoFieldErrors() {
        // Given
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getAllErrors()).thenReturn(new ArrayList<>());
        
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(exception.getMessage()).thenReturn("Validation failed");

        // When
        ResponseEntity<ProblemDetail> response = globalExceptionHandler.handleValidationException(exception, webRequest);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        
        ProblemDetail problemDetail = response.getBody();
        assertNotNull(problemDetail);
        assertEquals(HttpStatus.BAD_REQUEST.value(), problemDetail.getStatus());
        assertEquals("Erro de validação", problemDetail.getDetail());
        assertEquals("Erro de validação", problemDetail.getTitle());
        assertEquals(URI.create("https://keepguard.com/problems/validation-error"), problemDetail.getType());
        assertEquals("/api/test", problemDetail.getProperties().get("path"));
        assertNotNull(problemDetail.getProperties().get("timestamp"));
        
        @SuppressWarnings("unchecked")
        Map<String, String> errors = (Map<String, String>) problemDetail.getProperties().get("errors");
        assertNotNull(errors);
        assertTrue(errors.isEmpty());
    }

    @Test
    @DisplayName("Should handle path extraction correctly")
    void shouldHandlePathExtraction() {
        // Given
        when(webRequest.getDescription(false)).thenReturn("uri=/api/messages/123");
        NotFoundException exception = new NotFoundException("Resource not found");

        // When
        ResponseEntity<ProblemDetail> response = globalExceptionHandler.handleNotFoundException(exception, webRequest);

        // Then
        ProblemDetail problemDetail = response.getBody();
        assertNotNull(problemDetail);
        assertEquals("/api/messages/123", problemDetail.getProperties().get("path"));
    }

    @Test
    @DisplayName("Should handle MessageSendException with cause")
    void shouldHandleMessageSendExceptionWithCause() {
        // Given
        Exception cause = new RuntimeException("Network error");
        MessageSendException exception = new MessageSendException("Failed to send message", cause);

        // When
        ResponseEntity<ProblemDetail> response = globalExceptionHandler.handleMessageSendException(exception, webRequest);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        
        ProblemDetail problemDetail = response.getBody();
        assertNotNull(problemDetail);
        assertEquals("Failed to send message", problemDetail.getDetail());
    }

    @Test
    @DisplayName("Should handle ProviderConnectionException with cause")
    void shouldHandleProviderConnectionExceptionWithCause() {
        // Given
        Exception cause = new RuntimeException("Connection timeout");
        ProviderConnectionException exception = new ProviderConnectionException("Connection failed", cause);

        // When
        ResponseEntity<ProblemDetail> response = globalExceptionHandler.handleProviderConnectionException(exception, webRequest);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        
        ProblemDetail problemDetail = response.getBody();
        assertNotNull(problemDetail);
        assertEquals("Connection failed", problemDetail.getDetail());
    }
}
