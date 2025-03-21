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
import com.thames.finance_app.models.Moneda;
import com.thames.finance_app.models.Operacion;
import com.thames.finance_app.repositories.MonedaRepository;
import com.thames.finance_app.services.ClienteService;
import com.thames.finance_app.services.ReferidoService;
import com.thames.finance_app.services.TipoCambioService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OperacionMapper {
	
	private final ClienteService clienteService;
	private final ReferidoService referidoService;
	private final TipoCambioService tipoCambioService;
	private final MonedaRepository monedaRepository;
	private final PagoMapper pagoMapper;
	
    public Operacion toEntity(OperacionRequest request) {   
    	CuentaCorriente cuentaCliente = clienteService
				.obtenerPorNombre(request
				.getNombreCliente()).getCuentaCorriente();
    	Moneda monedaOrigen = monedaRepository
    			.findByCodigo(request.getMonedaOrigen())
    			.orElseThrow(() -> new RuntimeException("Moneda no encontrada"));
    	
    	Moneda monedaConversion = monedaRepository
    			.findByCodigo(request.getMonedaConversion())
    			.orElseThrow(() -> new RuntimeException("Moneda no encontrada"));
    
    	Operacion operacion = Operacion.builder()
                .fecha(LocalDateTime.now())
                .tipo(request.getTipo())
                .cuentaCorriente(cuentaCliente)
                .monedaOrigen(monedaOrigen)
                .montoOrigen(request.getMontoOrigen())
                .valorTipoCambio(request.getValorTipoCambio())
                .monedaConversion(monedaConversion)
                .montoConversion(request.getMontoOrigen().multiply(request.getValorTipoCambio()))
                .build();
    	
    	if(request.getNombreReferido() !=null) {      	
        	CuentaCorriente cuentaReferido = Optional.ofNullable(referidoService
					.obtenerPorNombre(request
					.getNombreReferido())
					.getCuentaCorriente()).orElse(null); 
        	
        	Moneda monedaReferido = monedaRepository
        			.findByCodigo(request.getMonedaReferido())
        			.orElseThrow(() -> new RuntimeException("Moneda no encontrada"));
  
        	operacion.setCuentaCorrienteReferido(cuentaReferido);
        	operacion.setMonedaReferido(monedaReferido);
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
                .tipo(operacion.getTipo().toString())
                .monedaOrigen(operacion.getMonedaOrigen().getCodigo())
                .montoOrigen(operacion.getMontoOrigen())
                
                .monedaConversion(operacion.getMonedaConversion().getCodigo())
                .montoConversion(operacion.getMontoConversion())
                .valorTipoCambio(operacion.getValorTipoCambio())
//                .valorTipoCambio(tipoCambioService.obtenerTipoCambio(operacion.getMonedaOrigen(), operacion.getMonedaConversion(),
//						operacion.getTipo() == TipoOperacion.COMPRA ? true : false ))
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
    									.obtenerPorNombre(request
    									.getNombreCliente()).getCuentaCorriente();
    	CuentaCorriente cuentaReferido = Optional.ofNullable(referidoService
    														.obtenerPorNombre(request
    														.getNombreReferido())
    														.getCuentaCorriente()).orElse(null);
    	Moneda monedaOrigen = monedaRepository
    			.findByCodigo(request.getMonedaOrigen())
    			.orElseThrow(() -> new RuntimeException("Moneda no encontrada"));
    	
    	Moneda monedaConversion = monedaRepository
    			.findByCodigo(request.getMonedaConversion())
    			.orElseThrow(() -> new RuntimeException("Moneda no encontrada"));
    	
        operacion.setTipo(request.getTipo());
        operacion.setMonedaOrigen(monedaOrigen);
        operacion.setMontoOrigen(request.getMontoOrigen());
        operacion.setMonedaConversion(monedaConversion);
        operacion.setMontoConversion(tipoCambioService.convertirMoneda(monedaOrigen, monedaConversion,
																		request.getMontoOrigen(),
																		request.getTipo() == TipoOperacion.COMPRA ? true : false ));
//        operacion.setValorTipoCambio(tipoCambioService.obtenerTipoCambio(monedaOrigen, monedaConversion,
//														request.getTipo() == TipoOperacion.COMPRA ? true : false ));
        operacion.setValorTipoCambio(request.getValorTipoCambio());
        operacion.setCuentaCorriente(cuentaCliente);
        
        if(request.getNombreReferido() !=null) {     	
        	Moneda monedaReferido = monedaRepository
        			.findByCodigo(request.getMonedaReferido())
        			.orElseThrow(() -> new RuntimeException("Moneda no encontrada"));
        	
            operacion.setCuentaCorrienteReferido(cuentaReferido);
            operacion.setPuntosReferido(request.getPuntosReferido());
            operacion.setMonedaReferido(monedaReferido);
//            operacion.setGananciaReferido(request.getPuntosReferido() != null ? 
//            							request.getPuntosReferido()
//            							.multiply(request.getMontoOrigen()) : BigDecimal.ZERO);
        }
        
        // Actualizar listas de pagos
        operacion.getPagosOrigen().clear();
        operacion.getPagosOrigen().addAll(pagoMapper.toPagoList(request.getPagosOrigen(), TipoPago.ORIGEN));
        operacion.getPagosConversion().clear();
        operacion.getPagosConversion().addAll(pagoMapper.toPagoList(request.getPagosConversion(), TipoPago.CONVERSION));
        
        return operacion;
    }
    

}

