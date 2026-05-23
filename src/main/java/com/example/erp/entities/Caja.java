package com.example.erp.entities;

import java.math.BigDecimal;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import com.example.erp.entities.enums.EstadoCaja;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

@Entity
public class Caja {

    @Id
    @UuidGenerator
    private UUID idCaja;

    @Enumerated(EnumType.STRING)
    private EstadoCaja estado;

    @Column(
            precision = 8,
            scale = 1
    )
    private BigDecimal montoInicial;

    @Column(
            precision = 8,
            scale = 1
    )
    private BigDecimal montoActual;

    @Column(
            precision = 6,
            scale = 1
    )
    private BigDecimal ingresosEfectivo;

    @Column(
            precision = 6,
            scale = 1
    )
    private BigDecimal ingresosOtros;

    @Column(
            precision = 6,
            scale = 1
    )
    private BigDecimal egresos;

}
