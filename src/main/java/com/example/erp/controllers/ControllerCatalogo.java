package com.example.erp.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.erp.services.CategoriaService;
import com.example.erp.services.ProductoService;

import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.erp.DTOs.response.ResponseCategoria;
import com.example.erp.DTOs.response.ResponseProducto;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.erp.DTOs.request.RequestCategoria;
import com.example.erp.DTOs.request.RequestProducto;


@Controller
@RequestMapping("/catalogo")
public class ControllerCatalogo {

    private final ProductoService productoService;
    private final CategoriaService categoriaService;

    public ControllerCatalogo(ProductoService productoService, CategoriaService categoriaService){
        this.productoService = productoService;
        this.categoriaService = categoriaService;
    }

    @GetMapping("/listarCategorias")
    public ResponseEntity<List<ResponseCategoria>> obtenerCategorias() {

        List<ResponseCategoria> listCategorias = categoriaService.obtenerCategorias();

        return ResponseEntity.ok(listCategorias);
    }

    @PostMapping("/crearCategoria")
    public ResponseEntity<ResponseCategoria> insertarCategoria(@RequestBody RequestCategoria requestCategoria) {
        
        ResponseCategoria responseCategoria = categoriaService.insertarCategoria(requestCategoria);
        
        return ResponseEntity.ok(responseCategoria);
    }
    
    @GetMapping("/listarProductos")
    public ResponseEntity<List<ResponseProducto>> obtenerProductos() {

        List<ResponseProducto> listProductos = productoService.obtenerProductos();

        return ResponseEntity.ok(listProductos);
    }

    @PostMapping("/crearProducto/categoria/{idCategoria}")
    public ResponseEntity<ResponseProducto> insertarProducto(@Valid @RequestBody RequestProducto requestProducto, @PathVariable UUID idCategoria) {

        ResponseProducto responseProducto = productoService.insertarProducto(requestProducto, idCategoria);

        return ResponseEntity.ok(responseProducto);
    }

}
