package com.thames.finance_app.services;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;


import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CajaService {
	
	
	//Se usa cuando se registra una operación.
	public void actualizarSaldoReal(Long cajaId, BigDecimal monto, boolean esIngreso) {
		//Aumenta o reduce el saldoReal según la operación.
		
	}
	
	public void actualizarSaldoDisponible(Long cajaId, BigDecimal monto, boolean esIngreso) {
		//Afecta el saldoDisponible cuando una operación es ejecutada.
		
		//Si la caja no tiene suficiente saldo disponible, permite saldo negativo
	}
}
