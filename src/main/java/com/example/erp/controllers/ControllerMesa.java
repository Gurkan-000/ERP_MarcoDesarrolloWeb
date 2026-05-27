package com.example.erp.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.erp.DTOs.response.ResponseMesa;
import com.example.erp.services.MesaService;

@RestController
@RequestMapping("/api/mesa")
public class ControllerMesa {

    private final MesaService mesaService;

    public ControllerMesa(MesaService mesaService) {
        this.mesaService = mesaService;
    }

    @GetMapping("/listar")
    public ResponseEntity<List<ResponseMesa>> listarMesas() {
        return ResponseEntity.ok(mesaService.obtenerMesas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseMesa> obtenerMesa(@PathVariable UUID id) {
        return ResponseEntity.ok(mesaService.obtenerMesa(id));
    }
}
