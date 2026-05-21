package com.example.erp.entities;

import java.math.BigDecimal;

import org.hibernate.validator.constraints.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class Mesa {

    private UUID idMesa;
    private Integer numero;

    @Column(
        length=30
    )
    private String estado;

    @OneToOne
    private Pedido pedido;

    private BigDecimal total;

    public Mesa(int numero, String estado) {
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
