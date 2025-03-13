package com.thames.finance_app.dtos;


import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.thames.finance_app.enums.TipoMovimiento;
import com.thames.finance_app.models.Moneda;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MovimientoCtaCteDTO {
   
	private Long id;
    private TipoMovimiento tipo;
    private Moneda moneda;
    private BigDecimal monto;
    private String descripcion;
    private LocalDateTime fecha;
	
}
