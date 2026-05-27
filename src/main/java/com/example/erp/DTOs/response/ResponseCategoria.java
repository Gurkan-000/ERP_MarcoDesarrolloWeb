package com.example.erp.DTOs.response;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseCategoria {

    private UUID idCategoria;

    private String nombre;

    private Integer productos;

}
