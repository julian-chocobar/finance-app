package com.thames.finance_app.dtos;

import java.math.BigDecimal;

import com.thames.finance_app.enums.EstadoOperacion;
import com.thames.finance_app.enums.Moneda;
import com.thames.finance_app.enums.TipoEntrega;
import com.thames.finance_app.enums.TipoOperacion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class OperacionRequest {

    @NotNull(message = "El tipo de operación es obligatorio.")
    private TipoOperacion tipo;

    @NotNull(message = "La cuenta corriente es obligatoria.")
    private Long cuentaCorrienteId;

    @NotNull(message = "La moneda de origen es obligatoria.")
    private Moneda monedaOrigen;

    @NotNull(message = "El monto de origen es obligatorio.")
    @Positive(message = "El monto de origen debe ser mayor a cero.")
    private BigDecimal montoOrigen;

    @PositiveOrZero(message = "El monto de origen ejecutado no puede ser negativo.")
    private BigDecimal montoOrigenEjecutado;

    @NotNull(message = "La moneda de conversión es obligatoria.")
    private Moneda monedaConversion;

    @NotNull(message = "El monto de conversión es obligatorio.")
    @Positive(message = "El monto de conversión debe ser mayor a cero.")
    private BigDecimal montoConversion;

    @PositiveOrZero(message = "El monto de conversión ejecutado no puede ser negativo.")
    private BigDecimal montoConversionEjecutado;

    @NotNull(message = "El tipo de cambio es obligatorio.")
    @Positive(message = "El tipo de cambio debe ser mayor a cero.")
    private BigDecimal tipoCambio;

    @NotNull(message = "El estado es obligatorio.")
    private EstadoOperacion estado;

    private TipoEntrega tipoEntrega;

    private Long cuentaCorrienteReferidoId;
    
    @PositiveOrZero(message = "El puntaje del referido no puede ser negativo.")
    private BigDecimal puntosReferido;
    
    private BigDecimal gananciaReferido;
    
    @NotNull(message = "La moneda del referido es obligatoria si hay ganancia.")
    private Moneda monedaReferido;

    private Long liquidadorId;

    @PositiveOrZero(message = "El monto del liquidador no puede ser negativo.")
    private BigDecimal montoLiquidador;
}


