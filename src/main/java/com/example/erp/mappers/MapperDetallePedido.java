package com.example.erp.mappers;

import com.example.erp.DTOs.response.ResponseDetallePedido;
import com.example.erp.entities.DetallePedido;

public class MapperDetallePedido {

    public static ResponseDetallePedido toDTO(DetallePedido detallePedido){
        return ResponseDetallePedido.builder()
        .idDetallePedido(detallePedido.getIdDetallePedido())
        .idProducto(detallePedido.getProducto().getIdProducto())
        .nombreProducto(detallePedido.getProducto().getNombre())
        .precioUnitario(detallePedido.getPrecioUnitario())
        .cantidad(detallePedido.getCantidad())
        .total(detallePedido.getTotal())
        .build();
    }   

}
