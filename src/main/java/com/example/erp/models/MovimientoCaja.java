package com.example.erp.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class MovimientoCaja {

    private String concepto;
    private String tipo;
    private String metodo;
    private double monto;

}
