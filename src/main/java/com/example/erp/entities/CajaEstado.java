package com.example.erp.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class CajaEstado {

    private String estado;
    private double montoInicial;
    private double montoActual;
    private double ingresosEfectivo;
    private double ingresosOtros;
    private double egresos;

}
