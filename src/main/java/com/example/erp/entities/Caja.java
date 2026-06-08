package com.example.erp.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import com.example.erp.entities.enums.EstadoCaja;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

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
    private BigDecimal ingresos;

    @Column(
            precision = 6,
            scale = 1
    )
    private BigDecimal egresos;

    private LocalDateTime fechaApertura;

    private LocalDateTime fechaCierre;

    @OneToMany(mappedBy = "caja", fetch = FetchType.LAZY)
    private final List<MovimientoCaja> movimientos = new ArrayList<>();

    public void addMovimiento(MovimientoCaja movimiento) {
        movimiento.setCaja(this);
        movimientos.add(movimiento);
    }

    public void recalcularMontoActual() {
        BigDecimal inicial = (this.montoInicial != null) ? this.montoInicial : BigDecimal.ZERO;
        BigDecimal entradas = (this.ingresos != null) ? this.ingresos : BigDecimal.ZERO;
        BigDecimal salidas = (this.egresos != null) ? this.egresos : BigDecimal.ZERO;

        this.montoActual = inicial.add(entradas).subtract(salidas);
    }

}
