package com.keepguard.ms_communication.application.service.exception;

public class UnsupportedMessageTypeException extends RuntimeException {
    public UnsupportedMessageTypeException(String message) {
        super(message);
    }
}