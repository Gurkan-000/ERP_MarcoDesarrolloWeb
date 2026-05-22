package com.example.erp.entities;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class Venta {
    @Column(
        length = 30
    )
    private String productoNombre;

    private Integer cantidad;

    private BigDecimal precioUnitario;

    @Column(
        precision = 5,
        scale = 2
    )
    private BigDecimal total;

    @Column(
        length = 30
    )
    private String metodoPago;

    @Column(
        length = 30
    )
    private String canal;

}
