package com.example.erp.DTOs.response;


import com.example.erp.entities.enums.Rol;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseUsuario {

    private String nombre;

    private String usuario;

    private Rol rol;

}
