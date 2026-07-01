package com.example.erp.entities;

import java.math.BigDecimal;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(
        name = "DetallesPedidosEspera"
)
public class DetallePedidoEspera {

    @Id
    @UuidGenerator
    private UUID idDetalleEspera;

    private Integer cantidad;

    private BigDecimal precioUnitario;

    private BigDecimal total;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "idPedidoEspera"
    )
    private PedidoEspera pedidoEspera;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "idProducto"
    )
    private Producto producto;

    public void calcularTotal() {
        precioUnitario = precioUnitario != null ? precioUnitario : BigDecimal.ZERO;
        cantidad = cantidad != null ? cantidad : 0;

        total = precioUnitario.multiply(new BigDecimal(cantidad));
    }

}
