package com.example.erp.entities;

import java.math.BigDecimal;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
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
        name = "DetallesPedidos"
)
public class DetallePedido {

    @Id
    @UuidGenerator
    private UUID idDetallePedido;

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

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(
            name = "idPedido"
    )
    private Pedido pedido;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(
            name = "idProducto"
    )
    private Producto producto;

    public void calcularTotal(){
        precioUnitario = precioUnitario != null ? precioUnitario : BigDecimal.ZERO;
        cantidad = cantidad != null ? cantidad : 0;

        total = precioUnitario.multiply(new BigDecimal(cantidad));
    }

}
