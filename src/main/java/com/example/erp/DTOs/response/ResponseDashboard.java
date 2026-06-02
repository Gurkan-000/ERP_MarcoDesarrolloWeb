package com.example.erp.DTOs.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter 
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseDashboard {


    private BigDecimal ventasHoy;
    private long pedidosTotales;
    private long mesasOcupadas;
    private long productosTotales;
    

    private BigDecimal ingresos;
    private BigDecimal egresos;
    

    private long pedidosLocal;
    private long pedidosLlevar;
    private long pedidosDelivery;

}