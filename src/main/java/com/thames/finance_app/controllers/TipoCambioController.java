package com.thames.finance_app.controllers;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.thames.finance_app.models.Moneda;
import com.thames.finance_app.services.MonedaService;
import com.thames.finance_app.services.TipoCambioService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/tipoCambio")
@RequiredArgsConstructor
public class TipoCambioController {

	private final TipoCambioService tipoCambioService;
	private final MonedaService monedaService;

	@GetMapping("/obtener")
	public ResponseEntity<Map<String, BigDecimal>> obtenerTipoCambio(
	        @RequestParam String monedaOrigen,
	        @RequestParam String monedaConversion,
	        @RequestParam boolean esCompra) {

		Moneda origen = monedaService.buscarPorCodigo(monedaOrigen);
		Moneda conversion = monedaService.buscarPorCodigo(monedaConversion);

	    BigDecimal tipoCambio = tipoCambioService.obtenerTipoCambio(origen, conversion, esCompra);
	    Map<String, BigDecimal> response = Collections.singletonMap("valor", tipoCambio);

	    return ResponseEntity.ok(response);
	}



}
