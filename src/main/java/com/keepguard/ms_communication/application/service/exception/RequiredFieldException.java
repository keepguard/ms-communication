package com.keepguard.ms_communication.application.service.exception;

public class RequiredFieldException extends RuntimeException {
    public RequiredFieldException(String message) {
        super(message);
    }
}