package com.example.erp.entities;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Pedido {

    private UUID idPedido;

    @OneToOne
    private Mesa mesa;

    @Column(
            length = 30
    )
    private String canal;

    @OneToMany(
        mappedBy="pedido"
    )
    private List<DetallePedido> detalles = new ArrayList<>();

    @Column(
        precision=5,
        scale=2
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

    public void agregarDetalle(Producto producto, int cantidad) {

        int cantidadFinal = cantidad > 0 ? cantidad : 1;
        DetallePedido existente = detalles.stream()
                .filter(detalle -> detalle.getProductoNombre().equalsIgnoreCase(producto.getNombre()))
                .findFirst()
                .orElse(null);

        BigDecimal precio = producto.getPrecio();
        BigDecimal totalDetalle = precio.multiply(new BigDecimal(cantidadFinal));

        if (existente == null) {
            detalles.add(new DetallePedido(cantidadFinal, producto.getNombre(), precio, totalDetalle));
        } else {
            existente.setCantidad(existente.getCantidad() + cantidadFinal);
            existente.setTotal(existente.getTotal() + totalDetalle);
        }

        recalcularTotal();
    }

    public void limpiar() {
        detalles.clear();
        total = new BigDecimal(0);
    }

    private void recalcularTotal() {
        total = detalles.stream().mapToDouble(DetallePedido::getTotal).sum();
    }

}
