package com.example.erp.entities;

import java.math.BigDecimal;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import com.example.erp.entities.enums.MetodoPago;
import com.example.erp.entities.enums.Tipo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

@Entity
@Table(
        name="MovimientosCaja"
)
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

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "idCaja")
    private Caja caja;

}
