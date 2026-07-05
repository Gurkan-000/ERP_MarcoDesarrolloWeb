package com.example.erp.services;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.erp.DTOs.response.ResponseDashboard;
import com.example.erp.DTOs.response.ResponsePedidoEspera;
import com.example.erp.DTOs.response.ResponsePedidoMensual;
import com.example.erp.entities.Caja;
import com.example.erp.entities.enums.EstadoCaja;
import com.example.erp.entities.enums.EstadoMesa;
import com.example.erp.entities.enums.TipoPedido;
import com.example.erp.exceptions.ReglaDeNegocioException;
import com.example.erp.mappers.MapperPedidoEspera;
import com.example.erp.mappers.MapperPedidoMensual;
import com.example.erp.repositories.CajaRepository;
import com.example.erp.repositories.MesaRepository;
import com.example.erp.repositories.PedidoEsperaRepository;
import com.example.erp.repositories.PedidoRepository;
import com.example.erp.repositories.ProductoRepository;

@Service
public class DashboardService {

    private final CajaRepository cajaRepository;
    private final PedidoRepository pedidoRepository;
    private final MesaRepository mesaRepository;
    private final ProductoRepository productoRepository;
    private final PedidoEsperaRepository pedidoEsperaRepository;

    public DashboardService(CajaRepository cajaRepository, PedidoRepository pedidoRepository,
            MesaRepository mesaRepository, ProductoRepository productoRepository,
            PedidoEsperaRepository pedidoEsperaRepository) {
        this.cajaRepository = cajaRepository;
        this.pedidoRepository = pedidoRepository;
        this.mesaRepository = mesaRepository;
        this.productoRepository = productoRepository;
        this.pedidoEsperaRepository = pedidoEsperaRepository;
    }

    @Transactional(readOnly = true)
    public ResponseDashboard obtenerEstadisticas() {

        Caja cajaActual = cajaRepository.findByEstado(EstadoCaja.ABIERTO)
                .orElseThrow(() -> new ReglaDeNegocioException("La caja no esta abierta"));

        BigDecimal ingresos = (cajaActual != null && cajaActual.getIngresos() != null) ? cajaActual.getIngresos()
                : BigDecimal.ZERO;
        BigDecimal egresos = (cajaActual != null && cajaActual.getEgresos() != null) ? cajaActual.getEgresos()
                : BigDecimal.ZERO;

        long pedidosLocal = pedidoRepository.countByTipoPedido(TipoPedido.LOCAL);
        long pedidosLlevar = pedidoRepository.countByTipoPedido(TipoPedido.LLEVAR);
        long pedidosDelivery = pedidoRepository.countByTipoPedido(TipoPedido.DELIVERY);

        long pedidosTotales = pedidoRepository.count();
        long mesasOcupadas = mesaRepository.countByEstado(EstadoMesa.OCUPADO);
        long totalMesas = mesaRepository.count();
        long productosTotales = productoRepository.count();

        return ResponseDashboard.builder()
                .ventasHoy(ingresos)
                .pedidosTotales(pedidosTotales)
                .mesasOcupadas(mesasOcupadas)
                .totalMesas(totalMesas)
                .productosTotales(productosTotales)
                .ingresos(ingresos)
                .egresos(egresos)
                .pedidosLocal(pedidosLocal)
                .pedidosLlevar(pedidosLlevar)
                .pedidosDelivery(pedidosDelivery)
                .build();
    }

    public List<ResponsePedidoMensual> obtenerPedidosMes(Integer mes, Integer anio) {

        List<ResponsePedidoMensual> respuesta = pedidoRepository.obtenerPedidosMes(mes, anio)
                .stream()
                .map(MapperPedidoMensual::toDTO)
                .toList();

        return respuesta;
    }

    @Transactional(readOnly = true)
    public List<ResponsePedidoEspera> obtenerPedidosEnEspera() {

        return pedidoEsperaRepository.findAllByOrderByFechaHoraAsc().stream()
                .map(MapperPedidoEspera::toDTO)
                .toList();
    }
}
