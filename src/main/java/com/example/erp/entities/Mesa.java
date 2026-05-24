package com.example.erp.entities;

import java.math.BigDecimal;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import com.example.erp.entities.enums.EstadoMesa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@Entity
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

    @OneToOne
    @JoinColumn(name="idPedido")
    private Pedido pedido;

    @Column(
        precision=6,
        scale=1
    )
    private BigDecimal total;

    public Mesa(int numero, EstadoMesa estado) {
        this.numero = numero;
        this.estado = estado;
    }

    public void asignarPedido(Pedido pedido) {
        this.pedido = pedido;
        if (pedido != null && pedido.getMesa() == null) {
            pedido.setMesa(this);
        }
        actualizarTotal();
    }

    public void actualizarTotal() {
        total = pedido != null ? pedido.getTotal() : new BigDecimal(0);
    }

    public void limpiarPedido() {
        pedido = null;
        total = new BigDecimal(0);
    }
}
