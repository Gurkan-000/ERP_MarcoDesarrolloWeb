package com.example.erp.DTOs.response;

import java.util.UUID;

import com.example.erp.entities.enums.EstadoMesa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter @Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseMesa {

    private UUID idMesa;

    private Integer numero;

    private EstadoMesa estado;

}
