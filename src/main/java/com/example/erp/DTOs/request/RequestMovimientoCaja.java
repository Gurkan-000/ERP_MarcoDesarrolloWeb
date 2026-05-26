package com.example.erp.DTOs.request;

import java.math.BigDecimal;

import com.example.erp.entities.enums.MetodoPago;
import com.example.erp.entities.enums.Tipo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class RequestMovimientoCaja {

    @NotBlank(message="Campo concepto vacio")
    private String concepto;

    @NotNull(message="Tipo de movimiento invalido")
    private Tipo tipo;

    @NotNull(message="Metodo de pago invalido")
    private MetodoPago metodo;  

    @NotNull(message="Monto invalido")
    @Positive(message="Ingrese un monto mayor a cero")
    private BigDecimal monto;

}
