package com.thames.finance_app.dtos;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CtaCteResponse {
    private Long id;
    private Long clienteId;
    private BigDecimal saldoPeso;
    private BigDecimal saldoDolar;
    private BigDecimal saldoEuro;
    private BigDecimal saldoReal;
    private BigDecimal saldoCrypto;
}
