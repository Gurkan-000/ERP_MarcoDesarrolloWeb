package com.example.erp.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.erp.models.Mesa;
import com.example.erp.models.Pedido;
import com.example.erp.models.Pedido.DetallePedido;
import com.example.erp.models.Producto;
import com.example.erp.models.Venta;

@Service
public class VentaService {

    private final List<Venta> ventas = new ArrayList<>();
    private final List<Mesa> mesas = new ArrayList<>();
    private final CatalogoService catalogoService;
    private final CajaService cajaService;

    public VentaService(CatalogoService catalogoService, CajaService cajaService) {
        this.catalogoService = catalogoService;
        this.cajaService = cajaService;
        cargarMesas();
    }

    public List<Venta> obtenerVentas() {
        return ventas;
    }

    public List<Mesa> obtenerMesas() {
        return mesas;
    }

    public void agregarProductoMesa(Pedido pedido) {
        if (pedido == null || pedido.getMesaNumero() == null) {
            return;
        }

        Mesa mesa = buscarMesa(pedido.getMesaNumero());
        if (mesa == null) {
            return;
        }

        Producto producto = catalogoService.buscarProductoPorNombre(pedido.getProductoNombre());
        if (producto == null) {
            return;
        }

        Pedido pedidoMesa = mesa.getPedido();
        if (pedidoMesa == null) {
            pedidoMesa = new Pedido(mesa, "Salon");
            mesa.asignarPedido(pedidoMesa);
        }

        pedidoMesa.agregarDetalle(producto, pedido.getCantidad());
        mesa.actualizarTotal();
    }

    public void marcarMesaOcupada(int mesaNumero) {
        Mesa mesa = buscarMesa(mesaNumero);
        if (mesa == null || mesa.getPedido() == null || mesa.getPedido().getDetalles().isEmpty()) {
            return;
        }

        mesa.setEstado("Ocupada");
    }

    public void cobrarMesa(Pedido pedidoCobro) {
        if (pedidoCobro == null || pedidoCobro.getMesaNumero() == null) {
            return;
        }

        Mesa mesa = buscarMesa(pedidoCobro.getMesaNumero());
        if (mesa == null || mesa.getPedido() == null || mesa.getPedido().getDetalles().isEmpty()) {
            return;
        }

        String metodoPago = textoPorDefecto(pedidoCobro.getMetodoPago(), "Efectivo");
        generarVentasPorPedido(mesa.getPedido(), metodoPago, "Salon");

        mesa.limpiarPedido();
        mesa.setEstado("Libre");
    }

    public void cobrarPedidoRapido(Pedido pedidoRapido) {
        if (pedidoRapido == null) {
            return;
        }

        Producto producto = catalogoService.buscarProductoPorNombre(pedidoRapido.getProductoNombre());
        if (producto == null) {
            return;
        }

        String canal = textoPorDefecto(pedidoRapido.getCanal(), "Para Llevar");
        String metodoPago = textoPorDefecto(pedidoRapido.getMetodoPago(), "Efectivo");

        Pedido pedido = new Pedido(null, canal);
        pedido.agregarDetalle(producto, pedidoRapido.getCantidad());

        generarVentasPorPedido(pedido, metodoPago, canal);
    }

    private void generarVentasPorPedido(Pedido pedido, String metodoPago, String canal) {
        for (DetallePedido detalle : pedido.getDetalles()) {
            Venta venta = new Venta(detalle.getProductoNombre(),
                    detalle.getCantidad(),
                    detalle.getPrecioUnitario(),
                    detalle.getTotal(),
                    metodoPago,
                    canal);
            ventas.add(0, venta);
            cajaService.registrarIngresoVenta(venta);
            catalogoService.descontarStock(detalle.getProductoNombre(), detalle.getCantidad());
        }
    }

    private Mesa buscarMesa(int mesaNumero) {
        return mesas.stream()
                .filter(mesa -> mesa.getNumero() == mesaNumero)
                .findFirst()
                .orElse(null);
    }

    private String textoPorDefecto(String value, String fallback) {
        if (value == null || value.trim().isEmpty()) {
            return fallback;
        }

        return value.trim();
    }

    private void cargarMesas() {
        for (int i = 1; i <= 6; i++) {
            mesas.add(new Mesa(i, "Libre"));
        }
    }
}
