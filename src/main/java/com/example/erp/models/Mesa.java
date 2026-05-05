package com.example.erp.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class Mesa {

    private int numero;
    private String estado;
    private Pedido pedido;
    private double total;

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
        total = pedido != null ? pedido.getTotal() : 0;
    }

    public void limpiarPedido() {
        pedido = null;
        total = 0;
    }
}
