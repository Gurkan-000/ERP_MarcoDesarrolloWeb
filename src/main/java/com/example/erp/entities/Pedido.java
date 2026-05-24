package com.example.erp.entities;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name="Pedidos"
)
public class Pedido {

    @Id
    @UuidGenerator
    private UUID idPedido;

    @OneToOne(mappedBy="pedido")
    private Mesa mesa;

    @Column(
            length = 30
    )
    private String canal;

    @OneToMany(
            mappedBy = "pedido"
    )
    private List<DetallePedido> detalles = new ArrayList<>();

    @Column(
            precision = 5,
            scale = 2
    )
    private BigDecimal total;

    private Integer mesaNumero;

    private Integer cantidad;

    @Column(
            length = 30
    )
    private String metodoPago;

    public Pedido(Mesa mesa, String canal) {
        this.mesa = mesa;
        this.canal = canal;
    }

}
