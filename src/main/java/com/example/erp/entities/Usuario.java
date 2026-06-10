package com.example.erp.entities;


import com.example.erp.entities.enums.Rol;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Usuario {

    private String nombre;
    private String usuario;
    private String contrasena;
    private Rol rol;

}
