package com.example.erp.DTOs.response;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.example.erp.entities.enums.MetodoPago;
import com.example.erp.entities.enums.TipoPedido;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponsePedidoMensual {
    private LocalDate fecha_pedido;
    private TipoPedido tipo_pedido;
    private BigDecimal total;
    private MetodoPago metodo_pago;
}
