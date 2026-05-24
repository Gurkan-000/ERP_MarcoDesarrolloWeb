package com.example.erp.DTOs.response;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseProducto {

    private UUID idProducto;

    private String nombreProducto;

    private String nombreCategoria;

    private Integer stock;

    private BigDecimal precio;

}
