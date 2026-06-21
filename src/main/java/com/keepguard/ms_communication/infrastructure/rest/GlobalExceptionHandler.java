package com.keepguard.ms_communication.infrastructure.rest;

import com.keepguard.ms_communication.application.service.exception.AlreadyExistsException;
import com.keepguard.ms_communication.application.service.exception.MessageSendException;
import com.keepguard.ms_communication.application.service.exception.NotFoundException;
import com.keepguard.ms_communication.application.service.exception.ProviderConnectionException;
import com.keepguard.ms_communication.application.service.exception.ProviderNotFoundException;
import com.keepguard.ms_communication.application.service.exception.RequiredFieldException;
import com.keepguard.ms_communication.application.service.exception.UnsupportedMessageTypeException;
import com.keepguard.ms_communication.application.service.exception.EmailSendException;
import com.keepguard.ms_communication.application.service.exception.InvalidTemplateException;
import com.keepguard.ms_communication.application.service.exception.CommandOperationException;
import com.keepguard.ms_communication.application.service.exception.QueryOperationException;
import com.keepguard.ms_communication.application.service.template.TemplateProcessorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ProblemDetail> handleNotFoundException(NotFoundException ex, WebRequest request) {
        log.error("Recurso não encontrado: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setType(URI.create("https://keepguard.com/problems/not-found"));
        problemDetail.setTitle("Recurso não encontrado");
        problemDetail.setProperty("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        problemDetail.setProperty("path", request.getDescription(false).replace("uri=", ""));

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<ProblemDetail> handleAlreadyExistsException(AlreadyExistsException ex, WebRequest request) {
        log.error("Recurso já existe: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problemDetail.setType(URI.create("https://keepguard.com/problems/already-exists"));
        problemDetail.setTitle("Recurso já existe");
        problemDetail.setProperty("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        problemDetail.setProperty("path", request.getDescription(false).replace("uri=", ""));

        return ResponseEntity.status(HttpStatus.CONFLICT).body(problemDetail);
    }

    @ExceptionHandler(RequiredFieldException.class)
    public ResponseEntity<ProblemDetail> handleRequiredFieldException(RequiredFieldException ex, WebRequest request) {
        log.error("Campo obrigatório não informado: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problemDetail.setType(URI.create("https://keepguard.com/problems/required-field"));
        problemDetail.setTitle("Campo obrigatório não informado");
        problemDetail.setProperty("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        problemDetail.setProperty("path", request.getDescription(false).replace("uri=", ""));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

    @ExceptionHandler(UnsupportedMessageTypeException.class)
    public ResponseEntity<ProblemDetail> handleUnsupportedMessageTypeException(UnsupportedMessageTypeException ex, WebRequest request) {
        log.error("Tipo de mensagem não suportado: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problemDetail.setType(URI.create("https://keepguard.com/problems/unsupported-message-type"));
        problemDetail.setTitle("Tipo de mensagem não suportado");
        problemDetail.setProperty("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        problemDetail.setProperty("path", request.getDescription(false).replace("uri=", ""));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

    @ExceptionHandler(ProviderNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleProviderNotFoundException(ProviderNotFoundException ex, WebRequest request) {
        log.error("Provedor não encontrado: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setType(URI.create("https://keepguard.com/problems/provider-not-found"));
        problemDetail.setTitle("Provedor não encontrado");
        problemDetail.setProperty("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        problemDetail.setProperty("path", request.getDescription(false).replace("uri=", ""));

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
    }

    @ExceptionHandler(MessageSendException.class)
    public ResponseEntity<ProblemDetail> handleMessageSendException(MessageSendException ex, WebRequest request) {
        log.error("Erro ao enviar mensagem: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        problemDetail.setType(URI.create("https://keepguard.com/problems/message-send-error"));
        problemDetail.setTitle("Erro ao enviar mensagem");
        problemDetail.setProperty("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        problemDetail.setProperty("path", request.getDescription(false).replace("uri=", ""));

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
    }

    @ExceptionHandler(ProviderConnectionException.class)
    public ResponseEntity<ProblemDetail> handleProviderConnectionException(ProviderConnectionException ex, WebRequest request) {
        log.error("Erro de conexão com provedor: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage());
        problemDetail.setType(URI.create("https://keepguard.com/problems/provider-connection-error"));
        problemDetail.setTitle("Erro de conexão com provedor");
        problemDetail.setProperty("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        problemDetail.setProperty("path", request.getDescription(false).replace("uri=", ""));

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(problemDetail);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidationException(MethodArgumentNotValidException ex, WebRequest request) {
        log.error("Erro de validação: {}", ex.getMessage());

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Erro de validação");
        problemDetail.setType(URI.create("https://keepguard.com/problems/validation-error"));
        problemDetail.setTitle("Erro de validação");
        problemDetail.setProperty("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        problemDetail.setProperty("path", request.getDescription(false).replace("uri=", ""));
        problemDetail.setProperty("errors", errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

    @ExceptionHandler(EmailSendException.class)
    public ResponseEntity<ProblemDetail> handleEmailSendException(EmailSendException ex, WebRequest request) {
        log.error("Erro ao enviar email: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        problemDetail.setType(URI.create("https://keepguard.com/problems/email-send-error"));
        problemDetail.setTitle("Erro ao enviar email");
        problemDetail.setProperty("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        problemDetail.setProperty("path", request.getDescription(false).replace("uri=", ""));
        problemDetail.setProperty("errorCode", "EMAIL_SEND_ERROR");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
    }

    @ExceptionHandler(InvalidTemplateException.class)
    public ResponseEntity<ProblemDetail> handleInvalidTemplateException(InvalidTemplateException ex, WebRequest request) {
        log.error("Template inválido: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problemDetail.setType(URI.create("https://keepguard.com/problems/invalid-template"));
        problemDetail.setTitle("Template inválido");
        problemDetail.setProperty("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        problemDetail.setProperty("path", request.getDescription(false).replace("uri=", ""));
        problemDetail.setProperty("errorCode", "INVALID_TEMPLATE");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

    @ExceptionHandler(TemplateProcessorService.TemplateNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleTemplateNotFoundException(TemplateProcessorService.TemplateNotFoundException ex, WebRequest request) {
        log.error("Template não encontrado: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setType(URI.create("https://keepguard.com/problems/template-not-found"));
        problemDetail.setTitle("Template não encontrado");
        problemDetail.setProperty("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        problemDetail.setProperty("path", request.getDescription(false).replace("uri=", ""));
        problemDetail.setProperty("errorCode", "TEMPLATE_NOT_FOUND");

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
    }

    @ExceptionHandler(CommandOperationException.class)
    public ResponseEntity<ProblemDetail> handleCommandOperationException(CommandOperationException ex, WebRequest request) {
        log.error("Falha na operação de comando: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        problemDetail.setType(URI.create("https://keepguard.com/problems/command-operation-failed"));
        problemDetail.setTitle("Falha na operação de comando");
        problemDetail.setProperty("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        problemDetail.setProperty("path", request.getDescription(false).replace("uri=", ""));
        problemDetail.setProperty("errorCode", ex.getErrorCode());
        problemDetail.setProperty("operation", ex.getOperation());
        problemDetail.setProperty("context", ex.getContext());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
    }

    @ExceptionHandler(QueryOperationException.class)
    public ResponseEntity<ProblemDetail> handleQueryOperationException(QueryOperationException ex, WebRequest request) {
        log.error("Falha na operação de consulta: {}", ex.getMessage());

        // Se a causa raiz for NotFoundException, retornar 404
        if (ex.getCause() instanceof NotFoundException) {
            ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getCause().getMessage());
            problemDetail.setType(URI.create("https://keepguard.com/problems/not-found"));
            problemDetail.setTitle("Recurso não encontrado");
            problemDetail.setProperty("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            problemDetail.setProperty("path", request.getDescription(false).replace("uri=", ""));
            problemDetail.setProperty("errorCode", "RESOURCE_NOT_FOUND");
            problemDetail.setProperty("operation", ex.getOperation());
            problemDetail.setProperty("context", ex.getContext());

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
        }

        // Para outros tipos de QueryOperationException, retornar 500
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        problemDetail.setType(URI.create("https://keepguard.com/problems/query-operation-failed"));
        problemDetail.setTitle("Falha na operação de consulta");
        problemDetail.setProperty("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        problemDetail.setProperty("path", request.getDescription(false).replace("uri=", ""));
        problemDetail.setProperty("errorCode", ex.getErrorCode());
        problemDetail.setProperty("operation", ex.getOperation());
        problemDetail.setProperty("context", ex.getContext());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGenericException(Exception ex, WebRequest request) {
        log.error("Erro interno do servidor: {}", ex.getMessage(), ex);

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno do servidor");
        problemDetail.setType(URI.create("https://keepguard.com/problems/internal-server-error"));
        problemDetail.setTitle("Erro interno do servidor");
        problemDetail.setProperty("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        problemDetail.setProperty("path", request.getDescription(false).replace("uri=", ""));

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
    }
}