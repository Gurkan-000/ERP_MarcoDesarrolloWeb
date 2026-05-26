package com.example.erp.DTOs.request;


import com.example.erp.entities.enums.MetodoPago;
import com.example.erp.entities.enums.TipoPedido;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Builder
public class RequestPedido {

    @NotNull(message="Metodo de pago invalido")
    private MetodoPago metodoPago;

    @NotNull(message="Tipo de pedido invalido")
    private TipoPedido tipoPedido;

}
