package com.example.erp.mappers;

import java.math.BigDecimal;

import com.example.erp.DTOs.request.RequestAperturaCaja;
import com.example.erp.DTOs.response.ResponseAperturaCaja;
import com.example.erp.entities.Caja;
import com.example.erp.entities.enums.EstadoCaja;

public class MapperCaja {

    public static Caja toEntity(RequestAperturaCaja requestCaja) {

        return Caja.builder()
                .estado(EstadoCaja.ABIERTO)
                .montoInicial(requestCaja.getMontoInicial())
                .montoActual(requestCaja.getMontoInicial())
                .ingresos(new BigDecimal(0))
                .egresos(new BigDecimal(0))
                .build();

    }

    public static ResponseAperturaCaja toDTO(Caja caja) {

        return ResponseAperturaCaja.builder()
                .idCaja(caja.getIdCaja())
                .estado(caja.getEstado())
                .montoInicial(caja.getMontoInicial())
                .montoActual(caja.getMontoActual())
                .ingresos(caja.getIngresos())
                .egresos(caja.getEgresos())
                .build();

    }

}
