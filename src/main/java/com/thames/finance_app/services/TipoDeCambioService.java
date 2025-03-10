package com.thames.finance_app.services;

import org.springframework.stereotype.Service;

import com.thames.finance_app.enums.Moneda;
import com.thames.finance_app.models.TipoDeCambio;
import com.thames.finance_app.repositories.TipoDeCambioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TipoDeCambioService {

	private final TipoDeCambioRepository tipoDeCambioRepository;

	public TipoDeCambio obtenerPorMoneda(Moneda moneda) {
		return tipoDeCambioRepository.findByMoneda(moneda);
	}
	
}
