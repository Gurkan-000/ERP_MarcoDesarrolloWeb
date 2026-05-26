package com.example.erp.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.erp.DTOs.request.RequestAperturaCaja;
import com.example.erp.DTOs.request.RequestMovimientoCaja;
import com.example.erp.DTOs.response.ResponseAperturaCaja;
import com.example.erp.DTOs.response.ResponseCierreCaja;
import com.example.erp.DTOs.response.ResponseMovimientoCaja;
import com.example.erp.services.CajaService;

import jakarta.validation.Valid;


@Controller
@RequestMapping("/caja")
public class ControllerCaja {

    private final CajaService cajaService;

    public ControllerCaja(CajaService cajaService) {
        this.cajaService = cajaService;
    }

    @GetMapping("/movimientos/{idCaja}")
    public ResponseEntity<List<ResponseMovimientoCaja>> obtenerMovimientosPorIdCaja(@PathVariable UUID idCaja) {

        List<ResponseMovimientoCaja> movimientosCaja = cajaService.obtenerMovimientosPorIdCaja(idCaja);

        return ResponseEntity.ok(movimientosCaja);
    }

    @GetMapping("/{idCaja}")
    public ResponseEntity<ResponseAperturaCaja> obtenerCaja(@PathVariable UUID idCaja) {

        ResponseAperturaCaja responseMovimientoCaja = cajaService.obtenerCaja(idCaja);

        return ResponseEntity.ok(responseMovimientoCaja);
    }

    @PostMapping("/registrarMovimiento/{idCaja}")
    public ResponseEntity<ResponseMovimientoCaja> registrarMovimiento(@PathVariable UUID idCaja, @Valid @RequestBody RequestMovimientoCaja requestMovimiento) {

        ResponseMovimientoCaja responseMovimientoCaja = cajaService.registrarMovimiento(requestMovimiento, idCaja);

        return ResponseEntity.ok(responseMovimientoCaja);
    }

    @PostMapping("/aperturarCaja")
    public ResponseEntity<ResponseAperturaCaja> aperturarCaja(@Valid @RequestBody RequestAperturaCaja requestCaja) {

        ResponseAperturaCaja responseAperturarCaja = cajaService.aperturarCaja(requestCaja);

        return ResponseEntity.ok(responseAperturarCaja);
    }

    @PutMapping("/cerrarCaja/{idCaja}")
    public ResponseEntity<ResponseCierreCaja> cerrarCaja(@PathVariable UUID idCaja) {

        ResponseCierreCaja responseCerrarCaja = cajaService.cerrarCaja(idCaja);

        return ResponseEntity.ok(responseCerrarCaja);
    }

}
