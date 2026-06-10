package com.example.erp.DTOs.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import com.example.erp.entities.enums.MetodoPago;
import com.example.erp.entities.enums.TipoPedido;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseBoleta {

    private UUID idPedido;

    private TipoPedido tipoPedido;

    private MetodoPago metodoPago;

    private BigDecimal total;

    private List<ResponseDetallePedido> detalles;

    private LocalDate fecha;

    private LocalTime hora;

    private String mensaje;

}
