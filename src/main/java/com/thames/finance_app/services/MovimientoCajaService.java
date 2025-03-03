package com.thames.finance_app.services;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.thames.finance_app.enums.TipoMovimiento;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MovimientoCajaService {
	
	public void registrarMovimientoCaja(Long cajaId, BigDecimal monto, boolean esIngreso, 
							TipoMovimiento tipoMovimiento, Long operacionId){
		
		//Crea un nuevo MovimientoCaja para cada afectación de saldo. 
	
		//Relaciona el movimiento con la caja y la operación correspondiente.
	}

}
