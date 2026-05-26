package com.example.erp.mappers;

import com.example.erp.DTOs.request.RequestMovimientoCaja;
import com.example.erp.DTOs.response.ResponseMovimientoCaja;
import com.example.erp.entities.MovimientoCaja;

public class MapperMovimiento {

    public static MovimientoCaja toEntity(RequestMovimientoCaja requestMovimiento) {

        return MovimientoCaja.builder()
                .concepto(requestMovimiento.getConcepto())
                .tipo(requestMovimiento.getTipo())
                .metodo(requestMovimiento.getMetodo())
                .monto(requestMovimiento.getMonto())
                .build();

    }

    public static ResponseMovimientoCaja toDTO(MovimientoCaja movimiento) {

        return ResponseMovimientoCaja.builder()
                .idMovimientoCaja(movimiento.getIdMovimientoCaja())
                .concepto(movimiento.getConcepto())
                .tipo(movimiento.getTipo())
                .metodo(movimiento.getMetodo())
                .monto(movimiento.getMonto())
                .build(); 

    }

}
