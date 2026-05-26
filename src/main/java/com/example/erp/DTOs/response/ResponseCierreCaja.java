package com.example.erp.DTOs.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@Builder
public class ResponseCierreCaja {

    private BigDecimal ultimoMonto;

}
