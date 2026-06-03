package com.example.erp.entities;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import com.example.erp.entities.enums.MetodoPago;
import com.example.erp.entities.enums.TipoPedido;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder

@Table(
        name = "Pedidos"
)
public class Pedido {

    @Id
    @UuidGenerator
    private UUID idPedido;

    private TipoPedido tipoPedido;

    @Column(
            precision = 6,
            scale = 1
    )
    private BigDecimal total;

    @Enumerated(EnumType.STRING)
    private MetodoPago metodoPago;

    @OneToMany(
            mappedBy = "pedido",
            fetch = FetchType.LAZY
    )
    private final List<DetallePedido> detalles = new ArrayList<>();

    @OneToOne(mappedBy = "pedido", fetch = FetchType.LAZY)
    private Mesa mesa;

    public void addDetallePedido(DetallePedido detallePedido) {
        detallePedido.setPedido(this);
        detalles.add(detallePedido);
    }

    public void calcularTotal() {
        total = detalles.stream()
                .map(DetallePedido::getTotal)
                .reduce(BigDecimal.ZERO,BigDecimal::add);
    }

}
