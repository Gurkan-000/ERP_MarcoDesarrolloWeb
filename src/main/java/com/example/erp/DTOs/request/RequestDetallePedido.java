package com.example.erp.DTOs.request;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Builder
public class RequestDetallePedido {

    @NotNull(message="Cantidad invalida")
    @Positive(message="Ingrese una cantidad mayor a cero")
    private Integer cantidad;

    @NotNull(message="No se selecciono un producto")
    private UUID idProducto;

}
