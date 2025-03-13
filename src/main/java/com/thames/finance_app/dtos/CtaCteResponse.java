package com.thames.finance_app.dtos;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.thames.finance_app.models.CuentaCorriente.CuentaCorrienteBuilder;
import com.thames.finance_app.models.Moneda;

import lombok.Builder;
import lombok.Data;

@Data
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
    private List<MovimientoCtaCteDTO> movimientos;
    private long totalMovimientos;
    private int totalPaginas;
    
    public CuentaCorrienteBuilder saldoPorMoneda(Map<Moneda, BigDecimal> of) {
		
		return null;
	}
}
