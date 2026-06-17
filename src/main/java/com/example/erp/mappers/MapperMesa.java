package com.example.erp.mappers;

import com.example.erp.DTOs.response.ResponseMesa;
import com.example.erp.entities.Mesa;
import com.example.erp.entities.enums.EstadoMesa;

public class MapperMesa {

    public static Mesa toEntity(Integer numeroMesa){
        return Mesa.builder()
                .numero(numeroMesa)
                .estado(EstadoMesa.LIBRE)
                .build();
    }

    public static ResponseMesa toDTO(Mesa mesa) {
        return ResponseMesa.builder()
                .idMesa(mesa.getIdMesa())
                .numero(mesa.getNumero())
                .estado(mesa.getEstado())
                .build();
    }

}
