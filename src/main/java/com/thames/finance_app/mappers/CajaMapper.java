package com.thames.finance_app.mappers;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.thames.finance_app.dtos.CajaDTO;
import com.thames.finance_app.dtos.MovimientoCajaDTO;
import com.thames.finance_app.exceptions.BusinessException;
import com.thames.finance_app.models.Caja;
import com.thames.finance_app.models.MovimientoCaja;
import com.thames.finance_app.models.Operacion;
import com.thames.finance_app.repositories.CajaRepository;
import com.thames.finance_app.repositories.MonedaRepository;
import com.thames.finance_app.repositories.OperacionRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CajaMapper {
	
	private final MonedaRepository monedaRepository;
	private final CajaRepository cajaRepository;
	private final OperacionRepository operacionRepository;

	
	
	public CajaDTO toDTO(Caja caja) {		
		return CajaDTO.builder()
				.nombre(caja.getNombre())
				.saldoDisponible(caja.getSaldoDisponible())
				.saldoReal(caja.getSaldoReal())
				.moneda(caja.getMoneda().getCodigo())
				.build();		
	}
	
	public CajaDTO toDTO(Caja caja, Page<MovimientoCaja> movimientos) {		
		return CajaDTO.builder()
				.nombre(caja.getNombre())
				.saldoDisponible(caja.getSaldoDisponible())
				.saldoReal(caja.getSaldoReal())
				.moneda(caja.getMoneda().getCodigo())
				.movimientos(movimientos.getContent().stream()
						.map(this::toMovimientoDTO)
						.toList())
				.build();		
	}
	
	public Caja toEntity(CajaDTO dto) {
		return Caja.builder()
				.nombre(dto.getNombre())
				.saldoReal(dto.getSaldoReal())
				.saldoDisponible(dto.getSaldoDisponible())
				.moneda(monedaRepository.findByCodigo(dto.getMoneda())
						.orElseThrow(()-> new RuntimeException("Moneda no encontrada")))
				.build();	
	}
	
	public MovimientoCajaDTO toMovimientoDTO(MovimientoCaja movimiento) {
		return MovimientoCajaDTO.builder()
				.id(movimiento.getId())
				.fecha(movimiento.getFecha())
				.tipo(movimiento.getTipo())
				.nombreCaja(movimiento.getCaja().getNombre())
				.idOperacion(movimiento.getOperacion().getId())
				.monto(movimiento.getMonto())
				.montoEjecutado(movimiento.getMontoEjecutado())			
				.build();
	}
	
	public MovimientoCaja toMovimientoEntity(MovimientoCajaDTO dto) {
		
		Caja caja = cajaRepository.findByNombre(dto.getNombreCaja())
				.orElseThrow(() -> new BusinessException("Caja no encontrada"));
		
		Operacion operacion = operacionRepository.findById(dto.getIdOperacion())
				.orElseThrow(() -> new BusinessException("Operacion no encontrada"));
		
		return MovimientoCaja.builder()
				.id(dto.getId())
				.fecha(dto.getFecha())
				.tipo(dto.getTipo())
				.caja(caja)
				.operacion(operacion)
				.monto(dto.getMonto())
				.montoEjecutado(dto.getMontoEjecutado())	
				.build();
	}

}
