package com.thames.finance_app.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MonedaDTO {

	@NotNull(message = "La moneda debe tener nombre")
    private String nombre; // Nombre de la moneda
}
