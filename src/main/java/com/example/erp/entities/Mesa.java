package com.example.erp.entities;

import java.math.BigDecimal;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import com.example.erp.entities.enums.EstadoMesa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder

@Table(
    name="Mesas"
)
public class Mesa {

    @Id
    @UuidGenerator
    private UUID idMesa;

    private Integer numero;

    @Enumerated(EnumType.STRING)
    private EstadoMesa estado;

    @OneToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="idPedido")
    private Pedido pedido;

    @Column(
        precision=6,
        scale=1
    )
    private BigDecimal total;

}
