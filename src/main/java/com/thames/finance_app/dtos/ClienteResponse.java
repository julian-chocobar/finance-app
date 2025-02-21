package com.thames.finance_app.dtos;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClienteResponse {
	private Long id;
    private String nombre;
    private String telefono;
    private String email;
    private String direccion;

    // Información básica de la cuenta corriente
    private Long cuentaCorrienteId;
    private BigDecimal saldoPeso;
    private BigDecimal saldoDolar;
    private BigDecimal saldoEuro;
    private BigDecimal saldoReal;
    private BigDecimal saldoCrypto;
    
    
}
