package com.thames.finance_app.dtos;


import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.thames.finance_app.enums.Moneda;
import com.thames.finance_app.enums.TipoMovimiento;

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
public class MovimientoCtaCteResponse {
   
	private Long id;
    private TipoMovimiento tipo;
    private Moneda moneda;
    private BigDecimal monto;
    private String descripcion;
    private LocalDateTime fecha;
	
}
