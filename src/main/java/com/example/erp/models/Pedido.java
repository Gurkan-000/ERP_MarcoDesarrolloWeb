package com.example.erp.models;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class Pedido {

    private Mesa mesa;
    private String canal;
    private List<DetallePedido> detalles = new ArrayList<>();
    private double total;

    private Integer mesaNumero;
    private String productoNombre;
    private int cantidad;
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

        double precio = producto.getPrecio();
        double totalDetalle = precio * cantidadFinal;

        if (existente == null) {
            detalles.add(new DetallePedido(producto.getNombre(), cantidadFinal, precio, totalDetalle));
        } else {
            existente.setCantidad(existente.getCantidad() + cantidadFinal);
            existente.setTotal(existente.getTotal() + totalDetalle);
        }

        recalcularTotal();
    }

    public void limpiar() {
        detalles.clear();
        total = 0;
    }

    private void recalcularTotal() {
        total = detalles.stream().mapToDouble(DetallePedido::getTotal).sum();
    }

}
