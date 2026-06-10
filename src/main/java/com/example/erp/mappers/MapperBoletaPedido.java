package com.example.erp.mappers;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.example.erp.DTOs.response.ResponseBoleta;
import com.example.erp.DTOs.response.ResponseDetallePedido;
import com.example.erp.entities.Pedido;

public class MapperBoletaPedido {

    public static ResponseBoleta toDTO(Pedido pedido, List<ResponseDetallePedido> detalles) {
        return ResponseBoleta.builder()
                .idPedido(pedido.getIdPedido())
                .tipoPedido(pedido.getTipoPedido())
                .metodoPago(pedido.getMetodoPago())
                .total(pedido.getTotal())
                .detalles(detalles)
                .fecha(LocalDate.now())
                .hora(LocalTime.now())
                .mensaje("Cobrado Exitosamente")
                .build();
    }

}
