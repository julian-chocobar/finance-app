package com.thames.finance_app.dtos;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.thames.finance_app.models.CuentaCorriente.CuentaCorrienteBuilder;
import com.thames.finance_app.models.Moneda;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CtaCteDTO {

    private Long id;

	@NotNull(message = "El clienteId no puede ser nulo")
    private String titularNombre;

    private Map<Moneda, BigDecimal> saldoPorMoneda;

    private List<MovimientoCtaCteDTO> movimientos;

    private long totalMovimientos;

    private int totalPaginas;

    public CuentaCorrienteBuilder saldoPorMoneda(Map<Moneda, BigDecimal> of) {
		return null;
	}
}
