package com.keepguard.ms_communication.application.service.exception;

public class ProviderConnectionException extends RuntimeException {
    public ProviderConnectionException(String message) {
        super(message);
    }

    public ProviderConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}