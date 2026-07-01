package com.example.erp.mappers;

import com.example.erp.DTOs.request.RequestPedidoEspera;
import com.example.erp.DTOs.response.ResponsePedidoEspera;
import com.example.erp.entities.PedidoEspera;
import com.example.erp.entities.Usuario;

public class MapperPedidoEspera {

    public static PedidoEspera toEntity(RequestPedidoEspera requestPedidoEspera, Usuario usuarioCreador) {
        return PedidoEspera.builder()
                .tipoPedido(requestPedidoEspera.getTipoPedido())
                .usuarioCreador(usuarioCreador)
                .build();
    }

    public static ResponsePedidoEspera toDTO(PedidoEspera pedidoEspera) {
        return ResponsePedidoEspera.builder()
                .idPedidoEspera(pedidoEspera.getIdPedidoEspera())
                .tipoPedido(pedidoEspera.getTipoPedido())
                .total(pedidoEspera.getTotal())
                .fechaHora(pedidoEspera.getFechaHora())
                .nombreUsuarioCreador(
                        pedidoEspera.getUsuarioCreador() != null
                                ? pedidoEspera.getUsuarioCreador().getNombre()
                                : null
                )
                .detalles(
                        pedidoEspera.getDetalles().stream()
                                .map(MapperDetallePedidoEspera::toDTO)
                                .toList()
                )
                .build();
    }

}
