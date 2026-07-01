package com.example.erp.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import com.example.erp.entities.enums.TipoPedido;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representa un pedido de tipo LLEVAR/DELIVERY que aun no ha sido cobrado y se
 * encuentra a la espera. Se persiste en base de datos para que cualquier
 * usuario autenticado (desde cualquier dispositivo) pueda visualizarlo,
 * a diferencia del antiguo manejo solo en sessionStorage del navegador.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(
        name = "PedidosEspera"
)
public class PedidoEspera {

    @Id
    @UuidGenerator
    private UUID idPedidoEspera;

    @Enumerated(EnumType.STRING)
    private TipoPedido tipoPedido;

    @Column(
            precision = 6,
            scale = 1
    )
    private BigDecimal total;

    @Column(
            name = "fecha_hora"
    )
    private LocalDateTime fechaHora;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "idUsuario"
    )
    private Usuario usuarioCreador;

    @Builder.Default
    @OneToMany(
            mappedBy = "pedidoEspera",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<DetallePedidoEspera> detalles = new ArrayList<>();

    public void addDetalle(DetallePedidoEspera detalle) {
        detalle.setPedidoEspera(this);
        detalles.add(detalle);
    }

    public void calcularTotal() {
        total = detalles.stream()
                .map(DetallePedidoEspera::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
