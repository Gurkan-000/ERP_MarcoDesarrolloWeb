package com.example.erp.entities;

import java.math.BigDecimal;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import com.example.erp.entities.enums.MetodoPago;
import com.example.erp.entities.enums.Tipo;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MovimientoCaja {

    @Id
    @UuidGenerator
    private UUID idMovimientoCaja;

    @Column(
            length = 100
    )
    private String concepto;

    @Enumerated(EnumType.STRING)
    private Tipo tipo;

    @Enumerated(EnumType.STRING)
    private MetodoPago metodo;

    @Column(
            precision = 6,
            scale = 1
    )
    private BigDecimal monto;

}
