package com.example.erp.DTOs.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestProducto {

    @NotBlank(message="Campo nombre esta vacio")
    private String nombre;

    @NotNull(message="Stock invalido")
    @PositiveOrZero(message="Ingrese un numero mayor igual a cero")
    private Integer stock;

    @NotNull(message="Precio invalido")
    @Positive(message="Ingrese un numero mayor a cero")
    private BigDecimal precio;

}
