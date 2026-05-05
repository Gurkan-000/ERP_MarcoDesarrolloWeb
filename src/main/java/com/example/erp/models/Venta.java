package com.example.erp.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class Venta {

    private String productoNombre;
    private int cantidad;
    private double precioUnitario;
    private double total;
    private String metodoPago;
    private String canal;

}
