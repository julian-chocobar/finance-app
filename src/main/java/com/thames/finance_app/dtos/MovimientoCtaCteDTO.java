package com.thames.finance_app.dtos;


import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.thames.finance_app.enums.TipoMovimiento;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MovimientoCtaCteDTO {

	private Long id;
	private LocalDateTime fecha;
	private String nombreTitular;
    private TipoMovimiento tipo;
    private String moneda;
    private BigDecimal monto;


}
