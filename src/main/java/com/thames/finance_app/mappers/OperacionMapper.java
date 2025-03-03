package com.thames.finance_app.mappers;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.thames.finance_app.dtos.OperacionRequest;
import com.thames.finance_app.dtos.OperacionResponse;
import com.thames.finance_app.models.CuentaCorriente;
import com.thames.finance_app.models.Liquidador;
import com.thames.finance_app.models.MovimientoCaja;
import com.thames.finance_app.models.MovimientoCtaCte;
import com.thames.finance_app.models.Operacion;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OperacionMapper {
    
	public MovimientoCaja toMovimientoCaja() {
		
	}
	
	public MovimientoCtaCte toMovimientoCtaCte() {
		
	}
	
	public OperacionResponse toResponse(Operacion operacion) {
        return OperacionResponse.builder()
                .id(operacion.getId())
                .fecha(operacion.getFecha())
                .tipo(operacion.getTipo())
                .monedaOrigen(operacion.getMonedaOrigen())
                .montoOrigen(operacion.getMontoOrigen())
                .montoOrigenEjecutado(operacion.getMontoOrigenEjecutado())
                .monedaConversion(operacion.getMonedaConversion())
                .montoConversion(operacion.getMontoConversion())
                .montoConversionEjecutado(operacion.getMontoConversionEjecutado())
                .tipoCambio(operacion.getTipoCambio())
                .estado(operacion.getEstado())
                .tipoEntrega(
                    operacion.getTipoEntrega() != null ? 
                    operacion.getTipoEntrega() : null
                )
                .cuentaCorrienteId(operacion.getCuentaCorriente().getId())
                .cuentaCorrienteReferidoId(
                    operacion.getCuentaCorrienteReferido() != null ? 
                    operacion.getCuentaCorrienteReferido().getId() : null
                )
                .liquidadorId(
                    operacion.getLiquidador() != null ? 
                    operacion.getLiquidador().getId() : null
                )
                .montoLiquidador(
                    operacion.getMontoLiquidador() != null ? 
                    operacion.getMontoLiquidador() : null
                )
                .puntosReferido(
                    operacion.getPuntosReferido() != null ? 
                    operacion.getPuntosReferido() : null
                )
                .monedaReferido(
                    operacion.getMonedaReferido() != null ? 
                    operacion.getMonedaReferido() : null
                )
                .gananciaReferido(
                    operacion.getGananciaReferido() != null ? 
                    operacion.getGananciaReferido() : null
                )
                .build();
    }

	public Operacion toEntity(OperacionRequest request, CuentaCorriente cuentaCliente, CuentaCorriente cuentaReferido, Liquidador liquidador) {
	    return Operacion.builder()
	            .fecha(LocalDateTime.now())
	            .tipo(request.getTipo())
	            .monedaOrigen(request.getMonedaOrigen())
	            .montoOrigen(request.getMontoOrigen())
	            .monedaConversion(request.getMonedaConversion())
	            .montoConversion(request.getMontoConversion())
	            .tipoCambio(request.getTipoCambio())
	            .estado(request.getEstado())
	            .tipoEntrega(request.getTipoEntrega() != null ? request.getTipoEntrega() : null)
	            .cuentaCorriente(cuentaCliente)
	            .cuentaCorrienteReferido(cuentaReferido != null ? cuentaReferido : null)
	            .liquidador(liquidador)
	            .montoLiquidador(request.getMontoLiquidador() != null ? request.getMontoLiquidador() : null)
	            .puntosReferido(request.getPuntosReferido() != null ? request.getPuntosReferido() : null)
	            .monedaReferido(request.getMonedaReferido() != null ? request.getMonedaReferido() : null)
	            .gananciaReferido(request.getPuntosReferido() != null ? request.getPuntosReferido().multiply(request.getMontoOrigen()) : BigDecimal.ZERO)
	            .build();
	}

	public void updateEntity(Operacion operacion, OperacionRequest request, Liquidador liquidador, CuentaCorriente cuentaCliente, CuentaCorriente cuentaReferido) {
	    operacion.setTipo(request.getTipo());
	    operacion.setMonedaOrigen(request.getMonedaOrigen());
	    operacion.setMontoOrigen(request.getMontoOrigen());
	    operacion.setMonedaConversion(request.getMonedaConversion());
	    operacion.setMontoConversion(request.getMontoConversion());
	    operacion.setTipoCambio(request.getTipoCambio());
	    operacion.setEstado(request.getEstado());
	    operacion.setTipoEntrega(request.getTipoEntrega() != null ? request.getTipoEntrega() : null);
        operacion.setCuentaCorriente(cuentaCliente);
        operacion.setCuentaCorrienteReferido(cuentaReferido != null ? cuentaReferido : null);
	    operacion.setPuntosReferido(request.getPuntosReferido() != null ? request.getPuntosReferido() : null);
	    operacion.setMonedaReferido(request.getMonedaReferido() != null ? request.getMonedaReferido() : null);
	    operacion.setGananciaReferido(request.getPuntosReferido() != null ? request.getPuntosReferido().multiply(request.getMontoOrigen()) : BigDecimal.ZERO);
	    operacion.setLiquidador(liquidador);
	    operacion.setMontoLiquidador(request.getMontoLiquidador() != null ? request.getMontoLiquidador() : null);
	}



}

