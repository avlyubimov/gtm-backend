package com.gtm.gtm.common.error;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) { super(message); }
    public ValidationException(String message, Throwable cause) { super(message, cause); }
}
