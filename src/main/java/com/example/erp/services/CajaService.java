package com.example.erp.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.erp.models.CajaEstado;
import com.example.erp.models.MovimientoCaja;
import com.example.erp.models.Venta;

@Service
public class CajaService {

    private final CajaEstado estado = new CajaEstado();
    private final List<MovimientoCaja> movimientos = new ArrayList<>();

    public CajaService() {
        seedData();
    }

    public CajaEstado getEstado() {
        return estado;
    }

    public List<MovimientoCaja> getMovimientos() {
        return movimientos;
    }

    public void registrarMovimiento(MovimientoCaja movimiento) {
        if (movimiento == null || movimiento.getMonto() <= 0) {
            return;
        }

        if (isTurnoCerrado()) {
            return;
        }

        normalizeMovimiento(movimiento);
        movimientos.add(0, movimiento);
        actualizarEstado(movimiento);
        recalcularMontoActual();
    }

    public void registrarIngresoVenta(Venta venta) {
        if (venta == null || venta.getTotal() <= 0) {
            return;
        }

        String concepto = "Venta " + venta.getProductoNombre();
        MovimientoCaja movimiento = new MovimientoCaja(concepto, "Ingreso", venta.getMetodoPago(), venta.getTotal());
        registrarMovimiento(movimiento);
    }

    public void cerrarTurno() {
        estado.setEstado("Turno Cerrado");
    }

    public void abrirTurno(double montoInicial) {
        double monto = montoInicial >= 0 ? montoInicial : 0;
        estado.setEstado("Turno Abierto");
        estado.setMontoInicial(monto);
        estado.setIngresosEfectivo(0);
        estado.setIngresosOtros(0);
        estado.setEgresos(0);
        movimientos.clear();
        recalcularMontoActual();
    }

    private void actualizarEstado(MovimientoCaja movimiento) {
        String tipo = movimiento.getTipo();
        String metodo = movimiento.getMetodo();
        double monto = movimiento.getMonto();

        if ("Egreso".equalsIgnoreCase(tipo)) {
            estado.setEgresos(estado.getEgresos() + monto);
            return;
        }

        if ("Efectivo".equalsIgnoreCase(metodo)) {
            estado.setIngresosEfectivo(estado.getIngresosEfectivo() + monto);
        } else {
            estado.setIngresosOtros(estado.getIngresosOtros() + monto);
        }
    }

    private void normalizeMovimiento(MovimientoCaja movimiento) {
        String concepto = movimiento.getConcepto();
        if (concepto == null || concepto.trim().isEmpty()) {
            movimiento.setConcepto("Movimiento de caja");
        } else {
            movimiento.setConcepto(concepto.trim());
        }

        String tipo = movimiento.getTipo();
        if (tipo == null || tipo.trim().isEmpty()) {
            movimiento.setTipo("Ingreso");
        } else {
            movimiento.setTipo(tipo.trim());
        }

        String metodo = movimiento.getMetodo();
        if (metodo == null || metodo.trim().isEmpty()) {
            movimiento.setMetodo("Efectivo");
        } else {
            movimiento.setMetodo(metodo.trim());
        }
    }

    private boolean isTurnoCerrado() {
        String estadoActual = estado.getEstado();
        return estadoActual != null && "Turno Cerrado".equalsIgnoreCase(estadoActual.trim());
    }

    private void seedData() {
        estado.setEstado("Turno Abierto");
        estado.setMontoInicial(50.00);
        
        registrarMovimiento(new MovimientoCaja("Venta mostrador", "Ingreso", "Efectivo", 15.00));
        registrarMovimiento(new MovimientoCaja("Compra de hielo", "Egreso", "Efectivo", 6.00));
        registrarMovimiento(new MovimientoCaja("Pago delivery", "Ingreso", "Tarjeta", 22.00));
        
        recalcularMontoActual();
        
    }

    private void recalcularMontoActual() {
        double monto = estado.getMontoInicial()
                + estado.getIngresosEfectivo()
                + estado.getIngresosOtros()
                - estado.getEgresos();
        estado.setMontoActual(monto);
    }
}
