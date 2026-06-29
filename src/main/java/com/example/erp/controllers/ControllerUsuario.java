package com.example.erp.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.erp.DTOs.request.RequestRegister;
import com.example.erp.DTOs.response.ResponseUsuario;
import com.example.erp.services.UsuarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/usuario")
public class ControllerUsuario {

    private final UsuarioService usuarioService;

    public ControllerUsuario(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/listarUsuarios")
    public ResponseEntity<List<ResponseUsuario>> obtenerUsuarios() {
        List<ResponseUsuario> responseUsuarios = usuarioService.obtenerUsuarios();

        return ResponseEntity.ok(responseUsuarios);
    }

    @PostMapping("/crearUsuario")
    public ResponseEntity<ResponseUsuario> insertarUsuario(@Valid @RequestBody RequestRegister requestRegister) {

        ResponseUsuario responseUsuario = usuarioService.insertarUsuario(requestRegister);

        return ResponseEntity.ok(responseUsuario);
    }

}
