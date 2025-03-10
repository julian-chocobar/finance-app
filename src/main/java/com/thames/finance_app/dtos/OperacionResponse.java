package com.thames.finance_app.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thames.finance_app.enums.Moneda;
import com.thames.finance_app.enums.TipoOperacion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OperacionResponse {
    private Long id;
    private LocalDateTime fecha;
    private TipoOperacion tipo;
    private String nombreCliente;
    private Moneda monedaOrigen;
    private BigDecimal montoOrigen;  
    private Moneda monedaConversion;
//    private BigDecimal montoConversion;
    private BigDecimal valorTipoCambio;
    
    private List<PagoResponse> pagosOrigen;
    private BigDecimal totalPagosOrigen;
    
    private List<PagoResponse> pagosConversion;
    private BigDecimal totalPagosConversion;
    
    private String nombreReferido;
    private BigDecimal puntosReferido;
    private BigDecimal gananciaReferido;
    private Moneda monedaReferido;
    
    
//    private Long liquidadorId;
//    private BigDecimal montoLiquidador;
}
