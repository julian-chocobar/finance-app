package com.thames.finance_app.dtos;

import java.math.BigDecimal;
import java.util.List;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CajaDTO {
	
	@NotNull(message = "La caja debe tener nombre")
	private String nombre;
	
	private BigDecimal saldoReal;
	
	private BigDecimal saldoDisponible;
		
	@NotNull(message = "Se debe indicar la moneda.")
	private String moneda;
	
	private List<MovimientoCajaDTO> movimientos;
		
}
