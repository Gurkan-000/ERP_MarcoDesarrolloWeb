package com.example.erp.DTOs.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class RequestAperturaCaja {

    @NotNull(message="Monto inicial invalido")
    @PositiveOrZero(message="Ingresa un monto mayor igual a cero")
    private BigDecimal montoInicial;

}
