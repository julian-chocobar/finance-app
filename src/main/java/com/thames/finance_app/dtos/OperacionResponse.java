package com.thames.finance_app.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

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
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private String tipo;
    private String nombreCliente;
    private String monedaOrigen;
    private BigDecimal montoOrigen;
    private String monedaConversion;
    private BigDecimal montoConversion;
    private BigDecimal valorTipoCambio;

    private List<PagoDTO> pagosOrigen;
    private BigDecimal totalPagosOrigen;

    private List<PagoDTO> pagosConversion;
    private BigDecimal totalPagosConversion;

    private String nombreReferido;
    private BigDecimal puntosReferido;
    private BigDecimal gananciaReferido;
    private String monedaReferido;
    private String estado;


}
