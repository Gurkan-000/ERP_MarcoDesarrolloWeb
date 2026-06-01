package com.example.erp.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.erp.DTOs.request.RequestAperturaCaja;
import com.example.erp.DTOs.request.RequestMovimientoCaja;
import com.example.erp.DTOs.response.ResponseAperturaCaja;
import com.example.erp.DTOs.response.ResponseCierreCaja;
import com.example.erp.DTOs.response.ResponseMovimientoCaja;
import com.example.erp.entities.Caja;
import com.example.erp.entities.MovimientoCaja;
import com.example.erp.entities.enums.EstadoCaja;
import com.example.erp.entities.enums.Tipo;
import com.example.erp.exceptions.EntidadNoEncontradaException;
import com.example.erp.exceptions.ReglaDeNegocioException;
import com.example.erp.mappers.MapperCaja;
import com.example.erp.mappers.MapperMovimiento;
import com.example.erp.repositories.CajaRepository;
import com.example.erp.repositories.MovimientoCajaRepository;

@Service
public class CajaService {

    private final CajaRepository cajaRepository;
    private final MovimientoCajaRepository movimientoCajaRepository;

    public CajaService(CajaRepository cajaRepository, MovimientoCajaRepository movimientoCajaRepository) {
        this.cajaRepository = cajaRepository;
        this.movimientoCajaRepository = movimientoCajaRepository;
    }

    @Transactional(readOnly = true)
    public List<ResponseMovimientoCaja> obtenerMovimientosPorIdCaja(UUID idCaja) {

        Caja caja = cajaRepository.findById(idCaja)
                .orElseThrow(() -> new EntidadNoEncontradaException("Caja no encontrada"));

        return movimientoCajaRepository.findByCaja(caja).stream()
                .map(MapperMovimiento::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public ResponseAperturaCaja obtenerCaja(UUID idCaja) {

        Caja caja = cajaRepository.findById(idCaja)
                .orElseThrow(() -> new EntidadNoEncontradaException("Caja no encontrada"));

        return MapperCaja.toDTO(caja);

    }

    @Transactional(readOnly = true)
    public ResponseAperturaCaja obtenerCajaAbierta() {

        Caja caja = cajaRepository.findByEstado(EstadoCaja.ABIERTO)
                .orElseThrow(() -> new EntidadNoEncontradaException("No existe caja abierta"));

        return MapperCaja.toDTO(caja);
    }

    @Transactional(readOnly = true)
    public ResponseCierreCaja obtenerUltimaCajaCerrada() {

        Caja caja = cajaRepository.findTopByEstadoOrderByFechaCierreDesc(EstadoCaja.CERRADO)
                .orElseThrow(() -> new EntidadNoEncontradaException("No existe caja cerrada"));

        ResponseCierreCaja responseCierreCaja = ResponseCierreCaja.builder()
                .ultimoMonto(caja.getMontoActual())
                .fechaCierre(caja.getFechaCierre())
                .build();

        return responseCierreCaja;
    }

    @Transactional
    public ResponseMovimientoCaja registrarMovimiento(RequestMovimientoCaja requestMovimiento, UUID idCaja) {

        Caja caja = cajaRepository.findById(idCaja)
                .orElseThrow(() -> new EntidadNoEncontradaException("Caja no encontrada"));

        if (requestMovimiento.getTipo() == Tipo.INGRESO) {

            caja.setIngresos(caja.getIngresos().add(requestMovimiento.getMonto()));

        } else if (requestMovimiento.getTipo() == Tipo.EGRESO) {

            caja.setEgresos(caja.getEgresos().add(requestMovimiento.getMonto()));

        }

        caja.recalcularMontoActual();

        MovimientoCaja movimientoCaja = MapperMovimiento.toEntity(requestMovimiento);
        caja.addMovimiento(movimientoCaja);

        movimientoCaja = movimientoCajaRepository.save(movimientoCaja);

        return MapperMovimiento.toDTO(movimientoCaja);

    }

    @Transactional
    public void registrarMovimientoPedido(RequestMovimientoCaja requestMovimiento, UUID idCaja) {

        Caja caja = cajaRepository.findById(idCaja)
                .orElseThrow(() -> new EntidadNoEncontradaException("Caja no encontrada"));

        if (caja.getEstado() == EstadoCaja.CERRADO) {
            throw new ReglaDeNegocioException("No se puede registrar movimientos en una caja cerrada");
        }

        caja.setIngresos(caja.getIngresos().add(requestMovimiento.getMonto()));

        caja.recalcularMontoActual();

        MovimientoCaja movimientoCaja = MapperMovimiento.toEntity(requestMovimiento);
        caja.addMovimiento(movimientoCaja);

        movimientoCajaRepository.save(movimientoCaja);

    }

    @Transactional
    public ResponseAperturaCaja aperturarCaja(RequestAperturaCaja requestCaja) {

        if (cajaRepository.existsByEstado(EstadoCaja.ABIERTO)) {
            throw new ReglaDeNegocioException("Ya existe una caja abierta");
        }

        Caja caja = MapperCaja.toEntity(requestCaja);

        caja = cajaRepository.save(caja);

        return MapperCaja.toDTO(caja);
    }

    @Transactional
    public ResponseCierreCaja cerrarCaja(UUID idCaja) {

        Caja caja = cajaRepository.findById(idCaja)
                .orElseThrow(() -> new EntidadNoEncontradaException("Caja no encontrada"));

        caja.setEstado(EstadoCaja.CERRADO);
        caja.setFechaCierre(LocalDateTime.now());

        ResponseCierreCaja responseCierreCaja = ResponseCierreCaja.builder()
                .ultimoMonto(caja.getMontoActual())
                .fechaCierre(caja.getFechaCierre())
                .build();

        return responseCierreCaja;
    }

}
