package com.example.erp.DTOs.response;

import java.math.BigDecimal;
import java.util.UUID;

import com.example.erp.entities.enums.MetodoPago;
import com.example.erp.entities.enums.TipoPedido;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Builder
public class ResponsePedido {

    private UUID idPedido;

    private TipoPedido tipoPedido;

    private BigDecimal total;

    private MetodoPago metodoPago;

}
