package com.example.erp.services;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.erp.DTOs.response.ResponseDashboard;
import com.example.erp.entities.Caja;
import com.example.erp.entities.enums.EstadoCaja;
import com.example.erp.entities.enums.EstadoMesa;
import com.example.erp.entities.enums.TipoPedido;
import com.example.erp.repositories.CajaRepository;
import com.example.erp.repositories.MesaRepository;
import com.example.erp.repositories.PedidoRepository;
import com.example.erp.repositories.ProductoRepository;

@Service
public class DashboardService {

    private final CajaRepository cajaRepository;
    private final PedidoRepository pedidoRepository;
    private final MesaRepository mesaRepository;
    private final ProductoRepository productoRepository;

    public DashboardService(CajaRepository cajaRepository, PedidoRepository pedidoRepository,
                            MesaRepository mesaRepository, ProductoRepository productoRepository) {
        this.cajaRepository = cajaRepository;
        this.pedidoRepository = pedidoRepository;
        this.mesaRepository = mesaRepository;
        this.productoRepository = productoRepository;
    }

    @Transactional(readOnly = true)
    public ResponseDashboard obtenerEstadisticas() {
        // 1. Obtener la caja activa para ingresos/egresos (Gráfico de Barras y Ventas Totales)
        Caja cajaActual = cajaRepository.findByEstado(EstadoCaja.ABIERTO).orElse(null);
        
        BigDecimal ingresos = (cajaActual != null && cajaActual.getIngresos() != null) ? cajaActual.getIngresos() : BigDecimal.ZERO;
        BigDecimal egresos = (cajaActual != null && cajaActual.getEgresos() != null) ? cajaActual.getEgresos() : BigDecimal.ZERO;

        // 2. Conteo de pedidos para la gráfica de Dona
        long pedidosLocal = pedidoRepository.countByTipoPedido(TipoPedido.LOCAL);
        long pedidosLlevar = pedidoRepository.countByTipoPedido(TipoPedido.LLEVAR);
        long pedidosDelivery = pedidoRepository.countByTipoPedido(TipoPedido.DELIVERY);

        // 3. Totales generales (Tarjetas superiores)
        long pedidosTotales = pedidoRepository.count();
        long mesasOcupadas = mesaRepository.countByEstado(EstadoMesa.OCUPADO);
        long productosTotales = productoRepository.count();

        // 4. Construir y retornar el DTO
        return ResponseDashboard.builder()
                .ventasHoy(ingresos) // Asumimos que las ventas reflejan los ingresos actuales en caja
                .pedidosTotales(pedidosTotales)
                .mesasOcupadas(mesasOcupadas)
                .productosTotales(productosTotales)
                .ingresos(ingresos)
                .egresos(egresos)
                .pedidosLocal(pedidosLocal)
                .pedidosLlevar(pedidosLlevar)
                .pedidosDelivery(pedidosDelivery)
                .build();
    }
}