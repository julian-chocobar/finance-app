package com.thames.finance_app.mappers;

import org.springframework.stereotype.Component;

import com.thames.finance_app.dtos.CajaDTO;
import com.thames.finance_app.models.Caja;

@Component
public class CajaMapper {
	
	public CajaDTO toDTO(Caja caja) {		
		return CajaDTO.builder()
				.nombre(caja.getNombre())
				.saldoDisponible(caja.getSaldoDisponible())
				.saldoReal(caja.getSaldoReal())
				.moneda(caja.getMoneda())
				.build();		
	}
	
	public Caja toEntity(CajaDTO dto) {
		return Caja.builder()
				.nombre(dto.getNombre())
				.saldoReal(dto.getSaldoReal())
				.saldoDisponible(dto.getSaldoDisponible())
				.moneda(dto.getMoneda())
				.build();	
	}

}
