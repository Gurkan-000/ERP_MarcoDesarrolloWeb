package com.example.erp.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.erp.DTOs.request.RequestActualizarStockProducto;
import com.example.erp.DTOs.request.RequestCategoria;
import com.example.erp.DTOs.request.RequestProducto;
import com.example.erp.DTOs.response.ResponseCategoria;
import com.example.erp.DTOs.response.ResponseProducto;
import com.example.erp.services.CategoriaService;
import com.example.erp.services.ProductoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/catalogo")
public class ControllerCatalogo {

    private final ProductoService productoService;
    private final CategoriaService categoriaService;

    public ControllerCatalogo(ProductoService productoService, CategoriaService categoriaService) {
        this.productoService = productoService;
        this.categoriaService = categoriaService;
    }

    @GetMapping("/listarCategorias")
    public ResponseEntity<List<ResponseCategoria>> obtenerCategorias() {

        List<ResponseCategoria> listCategorias = categoriaService.obtenerCategorias();

        return ResponseEntity.ok(listCategorias);
    }

    @PostMapping("/crearCategoria")
    public ResponseEntity<ResponseCategoria> insertarCategoria(@Valid @RequestBody RequestCategoria requestCategoria) {

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

    @PutMapping("/cantidadProducto/{idProducto}")
    public ResponseEntity<ResponseProducto> actualizarStockProducto(@PathVariable UUID idProducto, @RequestBody RequestActualizarStockProducto requestStockProducto) {

        ResponseProducto responseProducto = productoService.actualizarProducto(requestStockProducto, idProducto);

        return ResponseEntity.ok(responseProducto);

    }

    @PutMapping("/editarCategoria/{idCategoria}")
    public ResponseEntity<ResponseCategoria> editarCategoria(@PathVariable UUID idCategoria,
                                                            @RequestBody RequestCategoria requestCategoria) {

        ResponseCategoria responseCategoria = categoriaService.editarCategoria(requestCategoria, idCategoria);

        return ResponseEntity.ok(responseCategoria);
    }

    @PutMapping("/editarProducto/{idProducto}/categoria/{idCategoria}")
    public ResponseEntity<ResponseProducto> editarProducto(@PathVariable UUID idProducto, @PathVariable UUID idCategoria,
            @RequestBody RequestProducto requestProducto) {

        ResponseProducto responseProducto = productoService.editarProducto(requestProducto, idProducto, idCategoria);

        return ResponseEntity.ok(responseProducto);
    }
    
    @DeleteMapping("/eliminarCategoria/{idCategoria}")
    public ResponseEntity<Void> eliminarCategoria(@PathVariable UUID idCategoria) {

        categoriaService.eliminarCategoria(idCategoria);

        return ResponseEntity.noContent().build();

    }

    @DeleteMapping("/eliminarProducto/{idProducto}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable UUID idProducto) {

        productoService.eliminarProducto(idProducto);

        return ResponseEntity.noContent().build();

    }

}
