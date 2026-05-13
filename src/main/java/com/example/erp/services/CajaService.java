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
        if (movimiento != null && !isTurnoCerrado()) {
            
            movimiento.setConcepto(movimiento.getConcepto() == null || movimiento.getConcepto().isBlank() ? 
                                    "Movimiento de caja" : movimiento.getConcepto().trim());

            if(!(movimiento.getTipo().equalsIgnoreCase("Egreso") && movimiento.getMonto() > estado.getMontoActual() || movimiento.getMonto() <= 0)) {
                movimientos.add(0, movimiento);
                actualizarEstado(movimiento);
                recalcularMontoActual();
            }

        }
    }

    public void registrarIngresoVenta(Venta venta) {
        if (venta != null && venta.getTotal() > 0) {
            String concepto = "Venta " + venta.getProductoNombre();
            MovimientoCaja movimiento = new MovimientoCaja(concepto, "Ingreso", venta.getMetodoPago(), venta.getTotal());
            registrarMovimiento(movimiento);
        }
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
        }else{
            if ("Efectivo".equalsIgnoreCase(metodo)) {
                estado.setIngresosEfectivo(estado.getIngresosEfectivo() + monto);
            } else {
                estado.setIngresosOtros(estado.getIngresosOtros() + monto);
            }
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
