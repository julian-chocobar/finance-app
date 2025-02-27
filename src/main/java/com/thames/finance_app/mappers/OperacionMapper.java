package com.thames.finance_app.mappers;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.thames.finance_app.dtos.OperacionRequest;
import com.thames.finance_app.dtos.OperacionResponse;
import com.thames.finance_app.models.Operacion;
import com.thames.finance_app.services.CtaCteService;
import com.thames.finance_app.services.LiquidadorService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OperacionMapper {
	
	private final CtaCteService ctaCteService;
	private final LiquidadorService liquidadorService;
	

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
                .saldoResultante(operacion.getSaldoResultante())
                .tipoEntrega(operacion.getTipoEntrega())
                .cuentaCorrienteId(operacion.getCuentaCorriente().getId())
                .cuentaCorrienteReferidoId(
                    operacion.getCuentaCorrienteReferido() != null ?
                    operacion.getCuentaCorrienteReferido().getId() : null
                )
                .liquidadorId(
                    operacion.getLiquidador() != null ?
                    operacion.getLiquidador().getId() : null
                )
                .montoLiquidador(operacion.getMontoLiquidador())
                .build();
    }

    public Operacion toEntity(OperacionRequest request, Long cuentaCorrienteId, Long cuentaCorrienteReferidoId, Long liquidadorId) {
        return Operacion.builder()
                .fecha(LocalDateTime.now())
                .tipo(request.getTipo())
                .monedaOrigen(request.getMonedaOrigen())
                .montoOrigen(request.getMontoOrigen())
                .montoOrigenEjecutado(request.getMontoOrigenEjecutado())
                .monedaConversion(request.getMonedaConversion())
                .montoConversion(request.getMontoConversion())
                .montoConversionEjecutado(request.getMontoConversionEjecutado())
                .tipoCambio(request.getTipoCambio())
                .estado(request.getEstado())
                .saldoResultante(request.getSaldoResultante())
                .tipoEntrega(request.getTipoEntrega())
                .cuentaCorriente(ctaCteService.obtenerPorId(cuentaCorrienteId))
                .cuentaCorrienteReferido(ctaCteService.obtenerPorId(cuentaCorrienteReferidoId))
                .liquidador(liquidadorService.obtenerPorId(liquidadorId))
                .montoLiquidador(request.getMontoLiquidador())
                .build();
    }

	public void updateEntity(Operacion operacion, OperacionRequest request) {
		// TODO Auto-generated method stub
		
	}
}

