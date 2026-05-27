package com.example.erp.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.erp.repositories.ProductoRepository;

import org.springframework.transaction.annotation.Transactional;

@RestController
@RequestMapping("/api/inventario")
public class ControllerInventario {

    private final ProductoRepository productoRepository;

    public ControllerInventario(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @PostMapping("/reiniciar")
    @Transactional
    public ResponseEntity<Void> reiniciarInventario() {
        // Here we would implement the logic to clear history or stock
        // For now, let's just say it clears stock of all products as an example
        productoRepository.findAll().forEach(p -> p.setStock(0));
        return ResponseEntity.ok().build();
    }
}
