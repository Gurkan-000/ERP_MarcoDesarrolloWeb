package com.example.erp.DTOs.request;

import jakarta.validation.constraints.NotNull;
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
public class RequestActualizarStockProducto {

    @NotNull(message="Stock invalido")
    @PositiveOrZero(message="Ingrese un stock mayor igual a cero")
    private Integer stock;

}
