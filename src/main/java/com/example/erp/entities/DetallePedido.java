package com.example.erp.entities;

import java.math.BigDecimal;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = "DetallesPedidos"
)
public class DetallePedido {

    @Id
    @UuidGenerator
    private UUID idDetallePedido;

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
            precision = 5,
            scale = 1
    )
    private BigDecimal total;

    @ManyToOne
    @JoinColumn(
            name = "idPedido"
    )
    private Pedido pedido;

    public DetallePedido(Integer cantidad, Pedido pedido, BigDecimal precioUnitario, String productoNombre, BigDecimal total) {
        this.cantidad = cantidad;
        this.pedido = pedido;
        this.precioUnitario = precioUnitario;
        this.productoNombre = productoNombre;
        this.total = total;
    }

}
