package com.thames.finance_app.dtos;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CtaCteRequest {
    
	@NotNull(message = "El clienteId no puede ser nulo")
    @Positive(message = "El clienteId debe ser mayor que 0")
    private Long clienteId;

    @DecimalMin(value = "0.00", inclusive = true, message = "El saldo inicial en pesos debe ser 0")
    private BigDecimal saldoPeso = BigDecimal.ZERO;

    @DecimalMin(value = "0.00", inclusive = true, message = "El saldo inicial en dólares debe ser 0")
    private BigDecimal saldoDolar = BigDecimal.ZERO;

    @DecimalMin(value = "0.00", inclusive = true, message = "El saldo inicial en euros debe ser 0")
    private BigDecimal saldoEuro = BigDecimal.ZERO;

    @DecimalMin(value = "0.00", inclusive = true, message = "El saldo inicial en reales debe ser 0")
    private BigDecimal saldoReal = BigDecimal.ZERO;

    @DecimalMin(value = "0.00", inclusive = true, message = "El saldo inicial en crypto debe ser 0")
    private BigDecimal saldoCrypto = BigDecimal.ZERO;
}
