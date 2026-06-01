package com.example.erp.services;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.erp.DTOs.request.RequestActualizarStockProducto;
import com.example.erp.DTOs.request.RequestProducto;
import com.example.erp.DTOs.response.ResponseProducto;
import com.example.erp.entities.Categoria;
import com.example.erp.entities.Producto;
import com.example.erp.exceptions.EntidadNoEncontradaException;
import com.example.erp.mappers.MapperProducto;
import com.example.erp.repositories.CategoriaRepository;
import com.example.erp.repositories.ProductoRepository;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    public ProductoService(ProductoRepository productoRepository, CategoriaRepository categoriaRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @Transactional(readOnly = true)
    public List<ResponseProducto> obtenerProductos() {

        return productoRepository.findAll().stream()
                .map(MapperProducto::toDTO)
                .toList();

    }

    @Transactional
    public ResponseProducto insertarProducto(RequestProducto requestProducto, UUID idCategoria) {

        Categoria categoria = categoriaRepository.findById(idCategoria)
                .orElseThrow(() -> new EntidadNoEncontradaException("Categoria no encontrado"));

        Producto producto = MapperProducto.toEntity(requestProducto);

        categoria.addProducto(producto);

        producto = productoRepository.save(producto);

        return MapperProducto.toDTO(producto);

    }

    @Transactional
    public ResponseProducto actualizarProducto(RequestActualizarStockProducto requestStockProducto, UUID idProducto) {

        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new EntidadNoEncontradaException("Producto no encontrado"));

        producto.setStock(requestStockProducto.getStock());

        return MapperProducto.toDTO(producto);

    }

    @Transactional
    public void eliminarProducto(UUID idProducto) {

        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new EntidadNoEncontradaException("Producto no encontrado"));

        productoRepository.delete(producto);

    }

    @Transactional
    public ResponseProducto editarProducto(RequestProducto requestProducto, UUID idProducto, UUID idCategoria) {

        Categoria categoria = categoriaRepository.findById(idCategoria)
                .orElseThrow(() -> new EntidadNoEncontradaException("Categoria no encontrado"));

        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new EntidadNoEncontradaException("Producto no encontrado"));

        producto.setNombre(requestProducto.getNombre());
        producto.setPrecio(requestProducto.getPrecio());

        producto.setCategoria(categoria);

        return MapperProducto.toDTO(producto);
    }

}
