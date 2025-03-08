package com.thames.finance_app.services;
 
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.thames.finance_app.dtos.OperacionRequest;
import com.thames.finance_app.dtos.OperacionResponse;
import com.thames.finance_app.dtos.PagoRequest;
import com.thames.finance_app.enums.TipoOperacion;
import com.thames.finance_app.mappers.OperacionMapper;
import com.thames.finance_app.mappers.PagoMapper;
import com.thames.finance_app.models.Caja;
import com.thames.finance_app.models.Operacion;
import com.thames.finance_app.models.Pago;
import com.thames.finance_app.repositories.OperacionRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OperacionService {
	
    private final OperacionRepository operacionRepository;
    private final CajaService cajaService;
    private final CtaCteService ctaCteService;
    private final PagoService pagoService;
    private final OperacionMapper operacionMapper;
    private final PagoMapper pagoMapper;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    @Transactional
    public OperacionResponse crearOperacion(OperacionRequest request) {	
        Operacion operacion = operacionMapper.toEntity(request);
        pagoService.vincularConOperacion(operacion);
        operacion = operacionRepository.save(operacion); 
        
        cajaService.impactoOperacion(operacion);             
        ctaCteService.impactoOperacion(operacion);
        
        if(operacion.getCuentaCorrienteReferido()!=null) {   
            ctaCteService.impactoOperacionReferido(operacion); 
        }       
        return operacionMapper.toResponse(operacion);
    }
       
    public OperacionResponse obtenerOperacion(Long id) {
        Optional<Operacion> operacion = operacionRepository.findById(id);
        return operacion.map(operacionMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Operación no encontrada"));
    }

    public void eliminarOperacion(Long id) {
        operacionRepository.deleteById(id);
    }

    public Page<OperacionResponse> listarOperaciones(Specification<Operacion> spec, Pageable pageable) {
        return operacionRepository.findAll(spec, pageable).map(operacionMapper::toResponse);
    }
    
    public OperacionResponse actualizarOperacion(Long id, OperacionRequest request) {
    	
        Operacion vieja = operacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Operación no encontrada"));

        Operacion nueva = operacionMapper.updateEntity(vieja, request);
        pagoService.vincularConOperacion(nueva);
        
        cajaService.actualizarPorCambioOperacion(vieja, nueva);
        ctaCteService.actualizarPorCambioOperacion(vieja, nueva);
        ctaCteService.revertirImpactoOperacionReferido(vieja);
        ctaCteService.impactoOperacionReferido(nueva);
                      
        operacionRepository.save(nueva);
        return operacionMapper.toResponse(nueva);
    }
    
    public OperacionResponse agregarPagoOrigen(OperacionRequest operacionRequest, PagoRequest pagoRequest) {
    	Operacion operacion = operacionMapper.toEntity(operacionRequest);
    	Pago pago = pagoMapper.toEntity(pagoRequest);
    	pagoService.vincularConOperacion(pago, operacion);
    	
    	Caja origen = cajaService.obtenerPorMoneda(operacion.getMonedaOrigen());
    	if (operacion.getTipo() == TipoOperacion.COMPRA) {
    		cajaService.actualizarSaldoDisponible(origen, pago.getValor(), true);
    		operacion.getPagosOrigen().add(pago);
    	}
    	if (operacion.getTipo() == TipoOperacion.VENTA) {
    		cajaService.actualizarSaldoDisponible(origen, pago.getValor(), false);
    		operacion.getPagosOrigen().add(pago);
    	}
    	
    	operacionRepository.save(operacion);
    	return operacionMapper.toResponse(operacion);  	
    }
    
    public OperacionResponse agregarPagoConversion(OperacionRequest operacionRequest, PagoRequest pagoRequest) {
    	Operacion operacion = operacionMapper.toEntity(operacionRequest);
    	Pago pago = pagoMapper.toEntity(pagoRequest);
    	pagoService.vincularConOperacion(pago, operacion);
    	
    	Caja origen = cajaService.obtenerPorMoneda(operacion.getMonedaConversion());
    	if (operacion.getTipo() == TipoOperacion.COMPRA) {
    		cajaService.actualizarSaldoDisponible(origen, pago.getValor(), false);
    		operacion.getPagosOrigen().add(pago);
    	}
    	if (operacion.getTipo() == TipoOperacion.VENTA) {
    		cajaService.actualizarSaldoDisponible(origen, pago.getValor(), true);
    		operacion.getPagosOrigen().add(pago);
    	}
    	
    	operacionRepository.save(operacion);
    	return operacionMapper.toResponse(operacion);  	
    }
    
    
        
    public LocalDateTime parsearFecha(String fecha) {
    	try {
    		return (fecha != null) ? LocalDateTime.of(LocalDate.parse(fecha, FORMATTER), LocalTime.MIN) : null;
    	} catch (DateTimeParseException e) {
    	    throw new IllegalArgumentException("Formato de fecha incorrecto. Usa dd-MM-yyyy");
    	}
    }
    
    public boolean existePorCuentaCorriente(Long id) {
    	return operacionRepository.existsByCuentaCorrienteId(id);
    }
    
    
          
}


