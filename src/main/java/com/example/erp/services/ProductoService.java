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
import com.example.erp.repositories.ProductoRepository;



@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    private final CategoriaService categoriaService;

    public ProductoService(ProductoRepository productoRepository, CategoriaService categoriaService) {
        this.productoRepository = productoRepository;
        this.categoriaService = categoriaService;
    }

    @Transactional(readOnly=true)
    public List<ResponseProducto> obtenerProductos() {

        return productoRepository.findAll().stream()
                .map(MapperProducto::toDTO)
                .toList();

    }

    public Producto obtenerProducto(UUID idProducto) {

        Producto producto = productoRepository.findById(idProducto)
                                                .orElseThrow(() -> new EntidadNoEncontradaException("Producto no encontrado")); 

        return producto;

    }

    @Transactional
    public ResponseProducto insertarProducto(RequestProducto requestProducto, UUID idCategoria){

        Categoria categoria = categoriaService.obtenerCategoria(idCategoria);

        Producto producto = MapperProducto.toEntity(requestProducto);

        categoria.addProducto(producto);

        producto = productoRepository.save(producto);

        return MapperProducto.toDTO(producto);

    }

    @Transactional
    public ResponseProducto actualizarProducto(RequestActualizarStockProducto requestStockProducto, UUID idProducto){

        Producto producto = obtenerProducto(idProducto);

        producto.setStock(requestStockProducto.getStock());

        return MapperProducto.toDTO(producto);

    }


}
