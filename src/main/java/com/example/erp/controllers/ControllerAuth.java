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

    public ControllerAuth(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/registrarAdmin")
    public ResponseEntity<ResponseAuth> registrarAdmin(@Valid @RequestBody RequestRegister request) {
        ResponseAuth response = authService.registrarAdmin(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/iniciarSesion")
    public ResponseEntity<ResponseAuth> iniciarSesion(@Valid @RequestBody RequestLogin request) {
        ResponseAuth response = authService.iniciarSesion(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/cerrarSesion")
    public ResponseEntity<String> cerrarSesion(HttpServletRequest request) {

        String authHeader = request.getHeader("Authorization");

        authService.cerrarSesion(authHeader);

        return ResponseEntity.ok("Sesión cerrada exitosamente.");
    }

}
