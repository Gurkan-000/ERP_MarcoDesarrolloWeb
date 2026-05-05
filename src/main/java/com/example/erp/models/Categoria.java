package com.example.erp.models;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class Categoria {

    private String nombre;
    private String estado;

    private List<Producto> productos = new ArrayList<>();

    public Categoria(String nombre, String estado){
        this.nombre = nombre;
        this.estado = estado;
    }

    public void agregarProducto(Producto producto){
        productos.add(producto);
    }

}
