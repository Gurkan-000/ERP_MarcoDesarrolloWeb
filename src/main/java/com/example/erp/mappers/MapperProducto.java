package com.example.erp.mappers;

import com.example.erp.DTOs.request.RequestProducto;
import com.example.erp.DTOs.response.ResponseProducto;
import com.example.erp.entities.Producto;

public class MapperProducto {

    public static ResponseProducto toDTO(Producto producto) {

        return ResponseProducto.builder()
                .idProducto(producto.getIdProducto())
                .nombreProducto(producto.getNombre())
                .nombreCategoria(producto.getCategoria().getNombre())
                .stock(producto.getStock())
                .precio(producto.getPrecio())
                .build();

    }

    public static Producto toEntity(RequestProducto requestProducto) {

        return Producto.builder()
                .nombre(requestProducto.getNombre())
                .stock(0)
                .precio(requestProducto.getPrecio())
                .build();

    }

}
