package com.thames.finance_app.dtos;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.thames.finance_app.enums.Moneda;
import com.thames.finance_app.models.CuentaCorriente.CuentaCorrienteBuilder;

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
    
    private Map<Moneda, BigDecimal> saldoPorMoneda;
    private List<MovimientoCtaCteResponse> movimientos;
    private long totalMovimientos;
    private int totalPaginas;
    
    public CuentaCorrienteBuilder saldoPorMoneda(Map<Moneda, BigDecimal> of) {
		
		return null;
	}
}
