package com.example.erp.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class Producto {

    private String nombre;
    private Categoria categoria;
    private double precio;
    private int stock;

}
