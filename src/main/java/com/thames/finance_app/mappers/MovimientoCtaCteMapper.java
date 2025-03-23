package com.thames.finance_app.mappers;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.thames.finance_app.dtos.MovimientoCtaCteDTO;
import com.thames.finance_app.enums.TipoMovimiento;
import com.thames.finance_app.models.Moneda;
import com.thames.finance_app.models.MovimientoCtaCte;
import com.thames.finance_app.models.Operacion;

@Component
public class MovimientoCtaCteMapper {
	
    public MovimientoCtaCteDTO toDTO(MovimientoCtaCte movimiento) {
        return MovimientoCtaCteDTO.builder()
                .id(movimiento.getId())
                .nombreTitular(movimiento.getCuentaCorriente().getTitular().getNombre())
                .tipo(movimiento.getTipoMovimiento())
                .moneda(movimiento.getMoneda())
                .monto(movimiento.getMonto())
                .fecha(movimiento.getFecha())
                .build();
    }
    
    public MovimientoCtaCte toEntity (Operacion operacion, TipoMovimiento tipo,
			Moneda moneda, BigDecimal monto) {
		return MovimientoCtaCte.builder()
				.fecha(operacion.getFechaCreacion())
				.tipoMovimiento(tipo)
				.cuentaCorriente(operacion.getCuentaCorriente())
				.operacion(operacion)
				.moneda(moneda)
				.monto(monto)
				.build();	
			}

}
