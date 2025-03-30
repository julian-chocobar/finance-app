package com.thames.finance_app.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MonedaDTO {

	@NotNull(message = "La moneda debe tener nombre")
    private String nombre; // Nombre de la moneda
	
	@NotNull(message = "La moneda debe tener código")
	private String codigo;
}
