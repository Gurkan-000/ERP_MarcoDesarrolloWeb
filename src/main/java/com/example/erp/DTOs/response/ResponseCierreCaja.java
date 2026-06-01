package com.example.erp.DTOs.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@Builder
public class ResponseCierreCaja {

    private BigDecimal ultimoMonto;

    private LocalDateTime fechaCierre;

}
