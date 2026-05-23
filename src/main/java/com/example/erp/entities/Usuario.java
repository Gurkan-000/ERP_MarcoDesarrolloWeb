package com.example.erp.entities;

import java.util.List;

import com.example.erp.entities.enums.Rol;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class Usuario {

    private String nombre;
    private String contrasena;
    private Rol rol;
    private List<String> vistas;

}
