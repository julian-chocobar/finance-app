package com.thames.finance_app.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thames.finance_app.enums.EstadoOperacion;
import com.thames.finance_app.enums.Moneda;
import com.thames.finance_app.enums.TipoEntrega;
import com.thames.finance_app.enums.TipoOperacion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OperacionResponse {
    private Long id;
    private LocalDateTime fecha;
    private TipoOperacion tipo;
    private Long cuentaCorrienteId;
    private Moneda monedaOrigen;
    private BigDecimal montoOrigen;
    private BigDecimal montoOrigenEjecutado;
    private Moneda monedaConversion;
    private BigDecimal montoConversion;
    private BigDecimal montoConversionEjecutado;
    private BigDecimal tipoCambio;
    private EstadoOperacion estado;
    private BigDecimal saldoResultante;
    private TipoEntrega tipoEntrega;
    private Long cuentaCorrienteReferidoId;
    private BigDecimal puntosReferido;
    private BigDecimal gananciaReferido;
    private Moneda monedaReferido;
    private Long liquidadorId;
    private BigDecimal montoLiquidador;
}
