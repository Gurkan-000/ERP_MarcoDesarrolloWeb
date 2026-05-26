package com.example.erp.mappers;

import com.example.erp.DTOs.request.RequestPedido;
import com.example.erp.DTOs.response.ResponsePedido;
import com.example.erp.entities.Pedido;

public class MapperPedido {

    public static Pedido toEntity(RequestPedido requestPedido) {
        return Pedido.builder()
                .tipoPedido(requestPedido.getTipoPedido())
                .metodoPago(requestPedido.getMetodoPago())
                .build();
    }

    public static ResponsePedido toDTO(Pedido pedido) {
        return ResponsePedido.builder()
                .idPedido(pedido.getIdPedido())
                .tipoPedido(pedido.getTipoPedido())
                .total(pedido.getTotal())
                .metodoPago(pedido.getMetodoPago())
                .build();
    }

}
