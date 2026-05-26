package com.example.erp.DTOs.response;

import java.math.BigDecimal;
import java.util.UUID;

import com.example.erp.entities.enums.MetodoPago;
import com.example.erp.entities.enums.Tipo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseMovimientoCaja {

    private UUID idMovimientoCaja;

    private String concepto;

    private Tipo tipo;

    private MetodoPago metodo;

    private BigDecimal monto;

}
