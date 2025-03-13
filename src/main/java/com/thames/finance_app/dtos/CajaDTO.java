package com.thames.finance_app.dtos;

import java.math.BigDecimal;

import com.thames.finance_app.models.Moneda;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CajaDTO {
	
	private BigDecimal saldoReal;
	
	private BigDecimal saldoDisponible;
		
	@NotNull(message = "Se debe indicar la moneda.")
	private Moneda moneda;

}
