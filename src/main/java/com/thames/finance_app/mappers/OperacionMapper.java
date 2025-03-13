package com.thames.finance_app.mappers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.thames.finance_app.dtos.OperacionRequest;
import com.thames.finance_app.dtos.OperacionResponse;
import com.thames.finance_app.enums.TipoOperacion;
import com.thames.finance_app.enums.TipoPago;
import com.thames.finance_app.models.CuentaCorriente;
import com.thames.finance_app.models.Operacion;
import com.thames.finance_app.services.ClienteService;
import com.thames.finance_app.services.TipoCambioService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OperacionMapper {
	
	private final ClienteService clienteService;
	private final TipoCambioService tipoCambioService;
	private final PagoMapper pagoMapper;
	
    public Operacion toEntity(OperacionRequest request) {   
    	CuentaCorriente cuentaCliente = clienteService
				.obtenerClientePorNombre(request
				.getNombreCliente()).getCuentaCorriente();
    	
    	Operacion operacion = Operacion.builder()
                .fecha(LocalDateTime.now())
                .tipo(request.getTipo())
                .cuentaCorriente(cuentaCliente)
                .monedaOrigen(request.getMonedaOrigen())
                .montoOrigen(request.getMontoOrigen())
                .monedaConversion(request.getMonedaConversion())
                .montoConversion(tipoCambioService.convertirMoneda(request.getMonedaOrigen(), request.getMonedaConversion(),
                												request.getMontoOrigen(),
																request.getTipo() == TipoOperacion.COMPRA ? true : false ))
                .valorTipoCambio(tipoCambioService.obtenerTipoCambio(request.getMonedaOrigen(), request.getMonedaConversion(),
                												request.getTipo() == TipoOperacion.COMPRA ? true : false ))
                .build();
    	
    	if(request.getNombreReferido() !=null) {      	
        	CuentaCorriente cuentaReferido = Optional.ofNullable(clienteService
					.obtenerClientePorNombre(request
					.getNombreReferido())
					.getCuentaCorriente()).orElse(null); 
  
        	operacion.setCuentaCorrienteReferido(cuentaReferido);
        	operacion.setMonedaReferido(request.getMonedaReferido());
        	operacion.setPuntosReferido(request.getPuntosReferido());      
        }       
    	
    	return operacion;
    }
		
    public OperacionResponse toResponse(Operacion operacion) {
    	
    	String nombreCliente = operacion.getCuentaCorriente().getTitular().getNombre();
    	BigDecimal totalEjecutadoOrigen = operacion.getTotalPagosOrigen();
    	BigDecimal totalEjecutadoConversion = operacion.getTotalPagosConversion();
    	    	
        OperacionResponse operacionResponse = OperacionResponse.builder()
                .id(operacion.getId())
                .fecha(operacion.getFecha())
                .nombreCliente(nombreCliente)
                .tipo(operacion.getTipo())
                .monedaOrigen(operacion.getMonedaOrigen())
                .montoOrigen(operacion.getMontoOrigen())
                
                .monedaConversion(operacion.getMonedaConversion())
                .montoConversion(operacion.getMontoConversion())
                .valorTipoCambio(tipoCambioService.obtenerTipoCambio(operacion.getMonedaOrigen(), operacion.getMonedaConversion(),
						operacion.getTipo() == TipoOperacion.COMPRA ? true : false ))
                .pagosOrigen(pagoMapper.toPagoResponseList(operacion.getPagosOrigen()))
                .totalPagosOrigen(totalEjecutadoOrigen)
                
                .pagosConversion(pagoMapper.toPagoResponseList(operacion.getPagosConversion()))
                .totalPagosConversion(totalEjecutadoConversion)
                .build();
        
        if (operacion.getCuentaCorrienteReferido() != null) {
        	String nombreReferido = operacion.getCuentaCorrienteReferido().getTitular().getNombre();
        	operacionResponse.setNombreReferido(nombreReferido);
        	operacionResponse.setPuntosReferido(operacion.getPuntosReferido());
        	operacionResponse.setMonedaReferido(operacion.getMonedaReferido());
        	operacionResponse.setGananciaReferido(operacion.getMontoOrigen());
        	
        }
        return operacionResponse;
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
        operacion.setMontoConversion(tipoCambioService.convertirMoneda(request.getMonedaOrigen(), request.getMonedaConversion(),
																		request.getMontoOrigen(),
																		request.getTipo() == TipoOperacion.COMPRA ? true : false ));
        operacion.setValorTipoCambio(tipoCambioService.obtenerTipoCambio(request.getMonedaOrigen(), request.getMonedaConversion(),
														request.getTipo() == TipoOperacion.COMPRA ? true : false ));
        operacion.setCuentaCorriente(cuentaCliente);
        operacion.setCuentaCorrienteReferido(cuentaReferido);
        operacion.setPuntosReferido(request.getPuntosReferido());
        operacion.setMonedaReferido(request.getMonedaReferido());
//        operacion.setGananciaReferido(request.getPuntosReferido() != null ? 
//        							request.getPuntosReferido()
//        							.multiply(request.getMontoOrigen()) : BigDecimal.ZERO);

        // Actualizar listas de pagos
        operacion.getPagosOrigen().clear();
        operacion.getPagosOrigen().addAll(pagoMapper.toPagoList(request.getPagosOrigen(), TipoPago.ORIGEN));
        operacion.getPagosConversion().clear();
        operacion.getPagosConversion().addAll(pagoMapper.toPagoList(request.getPagosConversion(), TipoPago.CONVERSION));
        
        return operacion;
    }
    

}

