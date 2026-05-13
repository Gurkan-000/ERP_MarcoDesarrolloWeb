package com.example.erp.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class DetallePedido {

    private String productoNombre;
    private int cantidad;
    private double precioUnitario;
    private double total;
}
