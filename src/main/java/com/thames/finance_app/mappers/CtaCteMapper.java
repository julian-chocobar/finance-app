package com.thames.finance_app.mappers;

import java.math.BigDecimal;
import java.util.HashMap;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.thames.finance_app.dtos.CtaCteDTO;
import com.thames.finance_app.dtos.MovimientoCtaCteDTO;
import com.thames.finance_app.enums.TipoMovimiento;
import com.thames.finance_app.exceptions.BusinessException;
import com.thames.finance_app.models.CuentaCorriente;
import com.thames.finance_app.models.Moneda;
import com.thames.finance_app.models.MovimientoCtaCte;
import com.thames.finance_app.models.Operacion;
import com.thames.finance_app.models.Titular;
import com.thames.finance_app.repositories.MonedaRepository;
import com.thames.finance_app.repositories.TitularRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CtaCteMapper {

	private final TitularRepository titularRepository;
	private final MonedaRepository monedaRepository;

    public CtaCteDTO toDTO(CuentaCorriente cuenta) {
        return CtaCteDTO.builder()
                .id(cuenta.getId())
                .titularNombre(cuenta.getTitular().getNombre())
                .saldoPorMoneda(new HashMap<>(cuenta.getSaldos())) // Copia el mapa para evitar modificaciones accidentales
                .build();
    }

    public CtaCteDTO toDTO(CuentaCorriente cuentaCorriente, Page<MovimientoCtaCte> movimientos) {
        return CtaCteDTO.builder()
                .id(cuentaCorriente.getId())
                .titularNombre(cuentaCorriente.getTitular().getNombre())
                .saldoPorMoneda(new HashMap<>(cuentaCorriente.getSaldos()))
                .movimientos(movimientos.getContent().stream()
                        .map(this::toMovimientoDTO)
                        .toList())
                .totalMovimientos(movimientos.getTotalElements())
                .totalPaginas(movimientos.getTotalPages())
                .build();
    }


    public MovimientoCtaCteDTO toMovimientoDTO(MovimientoCtaCte movimiento) {
        return MovimientoCtaCteDTO.builder()
                .id(movimiento.getId())
                .nombreTitular(movimiento.getCuentaCorriente().getTitular().getNombre())
                .tipo(movimiento.getTipoMovimiento())
                .moneda(movimiento.getMoneda().getCodigo())
                .monto(movimiento.getMonto())
                .fecha(movimiento.getFecha())
                .build();
    }

    public MovimientoCtaCte toMovimientoEntity(MovimientoCtaCteDTO dto) {

    	Titular titular = titularRepository.findByNombre(dto.getNombreTitular())
    			.orElseThrow(() -> new BusinessException("Titular no encontrado"));

    	Moneda moneda = monedaRepository.findByCodigo(dto.getMoneda())
    			.orElseThrow(() -> new BusinessException("Titular no encontrado"));


    	return MovimientoCtaCte.builder()
    			.id(dto.getId())
    			.fecha(dto.getFecha())
    			.cuentaCorriente(titular.getCuentaCorriente())
    			.moneda(moneda)
    			.monto(dto.getMonto())

    			.build();
    }

    public MovimientoCtaCte toMovimientoEntity (Operacion operacion, TipoMovimiento tipo,
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
