package com.example.erp.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.erp.DTOs.request.RequestAuthUsuario;
import com.example.erp.DTOs.request.RequestUsuario;
import com.example.erp.DTOs.response.ResponseUsuario;
import com.example.erp.services.UsuarioService;

import jakarta.validation.Valid;



@RestController
@RequestMapping("/usuario")
@CrossOrigin("*")
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

    @GetMapping("/sesionActiva")
    public ResponseEntity<ResponseUsuario> obtenerSesionActiva() {
        ResponseUsuario responseUsuario = usuarioService.obtenerSesionActiva();

        return ResponseEntity.ok(responseUsuario);
    }

    @PostMapping("/autenticar")
    public ResponseEntity<Void> autenticar(@Valid @RequestBody RequestAuthUsuario requestUsuario) {

        usuarioService.autenticar(requestUsuario);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/cerrarSesion")
    public ResponseEntity<Void> cerrarSesion() {

        usuarioService.cerrarSesion();

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/crearUsuario")
    public ResponseEntity<ResponseUsuario> insertarUsuario(@Valid @RequestBody RequestUsuario requestUsuario) {

        ResponseUsuario responseUsuario = usuarioService.insertarUsuario(requestUsuario);

        return ResponseEntity.ok(responseUsuario);
    }


}
