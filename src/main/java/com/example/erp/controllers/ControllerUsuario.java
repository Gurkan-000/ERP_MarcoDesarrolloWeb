package com.example.erp.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.erp.entities.Usuario;
import com.example.erp.services.UsuarioService;

@RestController
@RequestMapping("/api/usuario")
public class ControllerUsuario {

    private final UsuarioService usuarioService;

    public ControllerUsuario(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/listar")
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        return ResponseEntity.ok(usuarioService.listarUsuarios());
    }

    @PostMapping("/crear")
    public ResponseEntity<Void> crearUsuario(@RequestBody Usuario nuevoUsuario) {
        boolean creado = usuarioService.crearUsuario(nuevoUsuario);
        if (creado) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }
}
