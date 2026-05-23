package com.example.erp.exceptions;

import java.util.Arrays;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.erp.DTOs.response.ResponseError;

@RestControllerAdvice
public class GlobalException {

    @ExceptionHandler(EntidadNoEncontradaException.class)
    public ResponseEntity<ResponseError> entidadNoEncontrada(EntidadNoEncontradaException ex){

        ResponseError error = ResponseError.builder().CodigoHttp(404)
                                                        .mensajes(Arrays.asList(ex.getMessage()))
                                                        .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);

    }

}
