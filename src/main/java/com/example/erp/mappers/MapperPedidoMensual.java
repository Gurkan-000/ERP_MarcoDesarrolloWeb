package com.example.erp.mappers;

import com.example.erp.DTOs.response.ResponsePedidoMensual;
import com.example.erp.entities.Pedido;

public class MapperPedidoMensual {

    public static ResponsePedidoMensual toDTO(Pedido pedido){
        return ResponsePedidoMensual.builder()
                .fecha_pedido(pedido.getFechaPedido())
                .tipo_pedido(pedido.getTipoPedido())
                .total(pedido.getTotal())
                .metodo_pago(pedido.getMetodoPago())
                .build();
    }

}
