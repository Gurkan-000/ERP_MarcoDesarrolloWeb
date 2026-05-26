package com.example.erp.mappers;

import com.example.erp.DTOs.response.ResponseMesa;
import com.example.erp.entities.Mesa;

public class MapperMesa {

    public static ResponseMesa toDTO(Mesa mesa) {
        return ResponseMesa.builder()
                .idMesa(mesa.getIdMesa())
                .numero(mesa.getNumero())
                .estado(mesa.getEstado())
                .build();
    }

}
