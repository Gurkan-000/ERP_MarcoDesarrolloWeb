package com.example.erp.DTOs.response;

import java.math.BigDecimal;
import java.util.UUID;

import com.example.erp.entities.enums.EstadoCaja;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseAperturaCaja {

    private UUID idCaja;

    private EstadoCaja estado;

    private BigDecimal montoInicial;

    private BigDecimal montoActual;

    private BigDecimal ingresos;

    private BigDecimal egresos;

}
