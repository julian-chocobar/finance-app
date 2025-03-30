package com.thames.finance_app.dtos;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TipoCambioDTO {

	 private String codigoMonedaOrigen;
	 
	 private String codigoMonedaConversion;
	 
	 private BigDecimal valorCompra; // Valor de compra de la monedaOrigen en términos de monedaDestino
	    
	 private BigDecimal valorVenta; // Valor de venta de la monedaOrigen en términos de monedaDestino
}
