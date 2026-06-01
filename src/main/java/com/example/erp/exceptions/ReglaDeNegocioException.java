package com.example.erp.exceptions;

public class ReglaDeNegocioException extends RuntimeException{

    public ReglaDeNegocioException() {
    }

    public ReglaDeNegocioException(String message) {
        super(message);
    }

    public ReglaDeNegocioException(String message, Throwable cause) {
        super(message, cause);
    }
    
}