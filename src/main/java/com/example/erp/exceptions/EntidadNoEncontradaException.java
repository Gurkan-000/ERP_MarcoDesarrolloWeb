package com.example.erp.exceptions;

public class EntidadNoEncontradaException extends RuntimeException{

    public EntidadNoEncontradaException() {
    }

    public EntidadNoEncontradaException(String message) {
        super(message);
    }

    public EntidadNoEncontradaException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
