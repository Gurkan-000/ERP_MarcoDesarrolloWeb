package com.example.erp.entities;

import java.math.BigDecimal;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import com.example.erp.entities.enums.MetodoPago;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Venta {

    @Id
    @UuidGenerator
    private UUID idVenta;

    @Column(
        length = 30
    )
    private String productoNombre;

    private Integer cantidad;

    @Column(
        precision = 5,
        scale = 1
    )
    private BigDecimal precioUnitario;

    @Column(
        precision = 6,
        scale = 1
    )
    private BigDecimal total;

    @Enumerated(EnumType.STRING)
    private MetodoPago metodoPago;

}
