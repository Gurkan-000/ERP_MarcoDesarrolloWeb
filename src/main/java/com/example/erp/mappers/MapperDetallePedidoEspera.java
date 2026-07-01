package com.example.erp.mappers;

import com.example.erp.DTOs.response.ResponseDetallePedido;
import com.example.erp.entities.DetallePedidoEspera;

public class MapperDetallePedidoEspera {

    public static ResponseDetallePedido toDTO(DetallePedidoEspera detalle) {
        return ResponseDetallePedido.builder()
                .idDetallePedido(detalle.getIdDetalleEspera())
                .idProducto(detalle.getProducto().getIdProducto())
                .nombreProducto(detalle.getProducto().getNombre())
                .precioUnitario(detalle.getPrecioUnitario())
                .cantidad(detalle.getCantidad())
                .total(detalle.getTotal())
                .build();
    }

}
