package com.keepguard.ms_communication.application.service.exception;

/**
 * Exceção específica para erros no envio de emails
 * 
 * Seguindo os princípios de Clean Code e DDD:
 * - Nome descritivo e específico
 * - Herança de RuntimeException para não quebrar contratos
 * - Mensagens claras para facilitar debugging
 */
public class EmailSendException extends RuntimeException {

    public EmailSendException(String message) {
        super(message);
    }

    public EmailSendException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmailSendException(Throwable cause) {
        super(cause);
    }
}
