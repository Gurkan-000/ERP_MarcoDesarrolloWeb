package com.example.erp.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.erp.DTOs.request.RequestLogin;
import com.example.erp.DTOs.request.RequestRegister;
import com.example.erp.DTOs.response.ResponseAuth;
import com.example.erp.services.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class ControllerAuth {

    private final AuthService authService;
    
    // Inyección por constructor de nuestro servicio de autenticación
    public ControllerAuth(AuthService authService) {
        this.authService = authService;
    }

    // ENDPOINT PARA REGISTRAR USUARIOS
    // Ruta física: POST http://localhost:8080/api/auth/registrarAdmin
    @PostMapping("/registrarAdmin")
    public ResponseEntity<ResponseAuth> registrarAdmin(@Valid @RequestBody RequestRegister request) {
        ResponseAuth response = authService.registrarAdmin(request);
        return ResponseEntity.ok(response);
    }

    // ENDPOINT PARA INICIAR SESIÓN
    // Ruta física: POST http://localhost:8080/api/auth/iniciarSesion
    @PostMapping("/iniciarSesion")
    public ResponseEntity<ResponseAuth> iniciarSesion(@Valid @RequestBody RequestLogin request) {
        ResponseAuth response = authService.iniciarSesion(request);
        return ResponseEntity.ok(response);
    }

    // ENDPOINT PARA CERRAR SESION
    // Ruta física: POST http://localhost:8080/api/auth/cerrarSesion
    @PostMapping("/cerrarSesion")
    public ResponseEntity<String> cerrarSesion(HttpServletRequest request) {

        String authHeader = request.getHeader("Authorization");

        authService.cerrarSesion(authHeader);

        return ResponseEntity.ok("Sesión cerrada exitosamente.");
    }

}
