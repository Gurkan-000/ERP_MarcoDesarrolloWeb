package com.example.erp.DTOs.request;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestPedidoMensual {
    @Positive(message = "El mes debe ser un número positivo entre 1 y 12")
    private Integer mes;
    @Positive(message = "El año debe ser un número positivo")
    private Integer anio;


}
