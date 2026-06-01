package com.example.erp.exceptions;

public class AutenticacionException extends RuntimeException {

    public AutenticacionException() {
    }

    public AutenticacionException(String message) {
        super(message);
    }

    public AutenticacionException(String message, Throwable cause) {
        super(message, cause);
    }

}
