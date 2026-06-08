package com.example.erp.DTOs.response;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseDetallePedido {

    private UUID idDetallePedido;

    private UUID idProducto;

    private String nombreProducto;

    private BigDecimal precioUnitario;
    
    private Integer cantidad;

    private BigDecimal total;

}
