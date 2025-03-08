package com.thames.finance_app.mappers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.thames.finance_app.dtos.OperacionRequest;
import com.thames.finance_app.dtos.OperacionResponse;
import com.thames.finance_app.enums.TipoPago;
import com.thames.finance_app.models.CuentaCorriente;
import com.thames.finance_app.models.Operacion;
import com.thames.finance_app.services.ClienteService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OperacionMapper {
	
	private final ClienteService clienteService;
	private final PagoMapper pagoMapper;
		
    public OperacionResponse toResponse(Operacion operacion) {
    	
    	String nombreCliente = operacion.getCuentaCorriente().getCliente().getNombre();
    	String nombreReferido = operacion.getCuentaCorrienteReferido().getCliente().getNombre();
    	BigDecimal totalEjecutadoOrigen = operacion.getTotalPagosOrigen();
    	BigDecimal totalEjecutadoConversion = operacion.getTotalPagosConversion();
    	
    	
    	
        return OperacionResponse.builder()
                .id(operacion.getId())
                .fecha(operacion.getFecha())
                .nombreCliente(nombreCliente)
                .tipo(operacion.getTipo())
                .monedaOrigen(operacion.getMonedaOrigen())
                .montoOrigen(operacion.getMontoOrigen())
                
                .monedaConversion(operacion.getMonedaConversion())
                .montoConversion(operacion.getMontoConversion())
                .tipoCambio(operacion.getTipoCambio())
                
                .nombreReferido(nombreReferido != null ?
                				nombreReferido : null)

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
                .pagosOrigen(pagoMapper.toPagoResponseList(operacion.getPagosOrigen()))
                .totalPagosOrigen(totalEjecutadoOrigen)
                
                .pagosConversion(pagoMapper.toPagoResponseList(operacion.getPagosConversion()))
                .totalPagosConversion(totalEjecutadoConversion)
                .build();
    }

    public Operacion toEntity(OperacionRequest request) {   
    	CuentaCorriente cuentaCliente = clienteService
				.obtenerClientePorNombre(request
				.getNombreCliente()).getCuentaCorriente();
    	CuentaCorriente cuentaReferido = Optional.ofNullable(clienteService
									.obtenerClientePorNombre(request
									.getNombreReferido())
									.getCuentaCorriente()).orElse(null);
    	
    	return Operacion.builder()
                .fecha(LocalDateTime.now())
                .tipo(request.getTipo())
                .cuentaCorriente(cuentaCliente)
                .monedaOrigen(request.getMonedaOrigen())
                .montoOrigen(request.getMontoOrigen())
                .monedaConversion(request.getMonedaConversion())
                .montoConversion(request.getMontoConversion())
                .tipoCambio(request.getTipoCambio())
                .cuentaCorrienteReferido(cuentaReferido)
                .puntosReferido(request.getPuntosReferido())
                .monedaReferido(request.getMonedaReferido())
                .gananciaReferido(request.getPuntosReferido() != null ? request.getPuntosReferido().multiply(request.getMontoOrigen()) : BigDecimal.ZERO)
                .pagosOrigen(pagoMapper.toPagoList(request.getPagosOrigen(), TipoPago.ORIGEN))
                .pagosConversion(pagoMapper.toPagoList(request.getPagosConversion(), TipoPago.CONVERSION))
                .build();   	
    }
   
    public Operacion updateEntity(Operacion operacion, OperacionRequest request) {
    	CuentaCorriente cuentaCliente = clienteService
    									.obtenerClientePorNombre(request
    									.getNombreCliente()).getCuentaCorriente();
    	CuentaCorriente cuentaReferido = Optional.ofNullable(clienteService
    														.obtenerClientePorNombre(request
    														.getNombreReferido())
    														.getCuentaCorriente()).orElse(null);
    	
        operacion.setTipo(request.getTipo());
        operacion.setMonedaOrigen(request.getMonedaOrigen());
        operacion.setMontoOrigen(request.getMontoOrigen());
        operacion.setMonedaConversion(request.getMonedaConversion());
        operacion.setMontoConversion(request.getMontoConversion());
        operacion.setTipoCambio(request.getTipoCambio());
        operacion.setCuentaCorriente(cuentaCliente);
        operacion.setCuentaCorrienteReferido(cuentaReferido);
        operacion.setPuntosReferido(request.getPuntosReferido());
        operacion.setMonedaReferido(request.getMonedaReferido());
        operacion.setGananciaReferido(request.getPuntosReferido() != null ? 
        							request.getPuntosReferido()
        							.multiply(request.getMontoOrigen()) : BigDecimal.ZERO);

        // Actualizar listas de pagos
        operacion.getPagosOrigen().clear();
        operacion.getPagosOrigen().addAll(pagoMapper.toPagoList(request.getPagosOrigen(), TipoPago.ORIGEN));
        operacion.getPagosConversion().clear();
        operacion.getPagosConversion().addAll(pagoMapper.toPagoList(request.getPagosConversion(), TipoPago.CONVERSION));
        
        return operacion;
    }
    

}

