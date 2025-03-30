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

	 private Long id;
	 	 
	 private String codigoMonedaOrigen;
	 
	 private String codigoMonedaConversion;
	 
	 private BigDecimal valorCompra; // Valor de compra de la monedaOrigen en términos de monedaConversion
	    
	 private BigDecimal valorVenta; // Valor de venta de la monedaOrigen en términos de monedaConversion
}
