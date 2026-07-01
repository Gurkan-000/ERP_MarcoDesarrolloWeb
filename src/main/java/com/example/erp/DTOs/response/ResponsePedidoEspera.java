package com.example.erp.DTOs.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.example.erp.entities.enums.TipoPedido;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponsePedidoEspera {

    private UUID idPedidoEspera;

    private TipoPedido tipoPedido;

    private BigDecimal total;

    private LocalDateTime fechaHora;

    private String nombreUsuarioCreador;

    private List<ResponseDetallePedido> detalles;

}
