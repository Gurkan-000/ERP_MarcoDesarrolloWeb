package com.example.erp.exceptions;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.example.erp.DTOs.response.ResponseError;

@RestControllerAdvice
public class GlobalException {

    @ExceptionHandler(EntidadNoEncontradaException.class)
    public ResponseEntity<ResponseError> entidadNoEncontrada(EntidadNoEncontradaException ex) {

        ResponseError error = ResponseError.builder().CodigoHttp(404)
                .mensajes(Arrays.asList(ex.getMessage()))
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);

    }

    @ExceptionHandler(AutenticacionException.class)
    public ResponseEntity<ResponseError> autenticacionException(AutenticacionException ex) {

        ResponseError error = ResponseError.builder().CodigoHttp(400)
                .mensajes(Arrays.asList(ex.getMessage()))
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);

    }

    @ExceptionHandler(MethodArgumentNotValidException.class)

    public ResponseEntity<ResponseError> campoMetodoInvalido(MethodArgumentNotValidException ex) {

        List<String> mensajes = ex.getBindingResult().getAllErrors().stream()
                .map(e -> e.getDefaultMessage()).toList();

        ResponseError error = ResponseError.builder().CodigoHttp(400)
                .mensajes(mensajes)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);

    }

    @ExceptionHandler(ReglaDeNegocioException.class)
    public ResponseEntity<ResponseError> campoMetodoInvalido(ReglaDeNegocioException ex) {

        ResponseError error = ResponseError.builder().CodigoHttp(409)
                .mensajes(Arrays.asList(ex.getMessage()))
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);

    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ResponseError> badCredentials(BadCredentialsException ex) {

        ResponseError error = ResponseError.builder()
                .CodigoHttp(401)
                .mensajes(List.of("Credenciales inválidas"))
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ResponseError> noHandlerFound(NoResourceFoundException ex) {

        ResponseError error = ResponseError.builder()
                .CodigoHttp(404)
                .mensajes(List.of("El endpoint solicitado no existe"))
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

}
