package com.thames.finance_app.services;
 
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.thames.finance_app.dtos.OperacionRequest;
import com.thames.finance_app.dtos.OperacionResponse;
import com.thames.finance_app.dtos.PagoDTO;
import com.thames.finance_app.enums.TipoOperacion;
import com.thames.finance_app.enums.TipoPago;
import com.thames.finance_app.mappers.OperacionMapper;
import com.thames.finance_app.mappers.PagoMapper;
import com.thames.finance_app.models.Caja;
import com.thames.finance_app.models.CuentaCorriente;
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
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OperacionService {
	
    private final OperacionRepository operacionRepository;
    private final CajaService cajaService;
    private final CtaCteService ctaCteService;
    private final ClienteService clienteService;
    private final PagoService pagoService;
    private final OperacionMapper operacionMapper;
    private final PagoMapper pagoMapper;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    @Transactional
    public OperacionResponse crearOperacion(OperacionRequest request) {	
        Operacion operacion = operacionMapper.toEntity(request);
        operacion = operacionRepository.save(operacion);
        
        List<Pago> pagosOrigen = pagoMapper.toPagoList(request.getPagosOrigen(), TipoPago.ORIGEN);
        List<Pago> pagosConversion = pagoMapper.toPagoList(request.getPagosConversion(), TipoPago.CONVERSION);
        
        operacion.setPagosOrigen(pagosOrigen);
        operacion.setPagosConversion(pagosConversion);  
        pagoService.vincularConOperacion(operacion);
        
        operacion = operacionRepository.save(operacion); 
        
        cajaService.impactoOperacion(operacion);             
        ctaCteService.impactoOperacion(operacion);
        
        if(operacion.getCuentaCorrienteReferido()!=null) { 
            ctaCteService.impactoOperacionReferido(operacion); 
        }       
        return operacionMapper.toResponse(operacion);
    }
       
    public OperacionResponse obtenerResponsePorId(Long id) {
        Optional<Operacion> operacion = operacionRepository.findById(id);
        return operacion.map(operacionMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Operación no encontrada"));
    }
    
    public Operacion obtenerPorId(Long id) {
       Operacion operacion = operacionRepository.findById(id)
        		.orElseThrow(() -> new RuntimeException("Operación no encontrada"));
        return operacion;
    }

    public void eliminarOperacion(Long id) {
    	Operacion operacion = operacionRepository.getReferenceById(id);
    	cajaService.revertirImpactoOperacion(operacion);
    	ctaCteService.revertirImpactoOperacion(operacion);
    	ctaCteService.revertirImpactoOperacionReferido(operacion);
        operacionRepository.deleteById(id);
    }
    
    public void eliminarOperacionSinRevertir(Long id) {
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
        
        cajaService.revertirImpactoOperacion(vieja);
        cajaService.impactoOperacion(nueva);
        
        ctaCteService.revertirImpactoOperacion(vieja);
        ctaCteService.impactoOperacion(nueva);
        
        ctaCteService.revertirImpactoOperacionReferido(vieja);
        ctaCteService.impactoOperacionReferido(nueva);
                      
        operacionRepository.save(nueva);
        return operacionMapper.toResponse(nueva);
    }
    
  
    public OperacionResponse cambiarMontoOrigen(Long id, OperacionRequest request) {  	
        Operacion operacion = operacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Operación no encontrada"));
        cajaService.revertirImpactoOperacion(operacion);   
        operacion.setMontoOrigen(request.getMontoOrigen());
        cajaService.impactoOperacion(operacion);    	
        
        operacionRepository.save(operacion);
        return operacionMapper.toResponse(operacion);
    }
    
    public OperacionResponse cambiarCliente(Long id, OperacionRequest request) {
    	Operacion operacion = operacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Operación no encontrada"));
    	
    	CuentaCorriente cuentaNueva = clienteService
				.obtenerPorNombre(request
				.getNombreCliente()).getCuentaCorriente();
    	ctaCteService.revertirImpactoOperacion(operacion);
    	operacion.setCuentaCorriente(cuentaNueva);
    	ctaCteService.impactoOperacion(operacion);
    	
    	operacionRepository.save(operacion); 
    	return operacionMapper.toResponse(operacion);
    }
    
    
    public OperacionResponse agregarPagoOrigen(Long id, PagoDTO pagoRequest) {
    	Operacion operacion = operacionRepository.findById(id)
    			.orElseThrow(() -> new RuntimeException("Operación no encontrada"));;
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
    
    public OperacionResponse quitarPagoOrigen(Long id, PagoDTO pagoRequest) {
    	Operacion operacion = operacionRepository.findById(id)
    			.orElseThrow(() -> new RuntimeException("Operación no encontrada"));;
    	Pago pago = pagoMapper.toEntity(pagoRequest);
    	Caja origen = cajaService.obtenerPorMoneda(operacion.getMonedaOrigen());
    	if (operacion.getTipo() == TipoOperacion.COMPRA) {
    		cajaService.actualizarSaldoDisponible(origen, pago.getValor(), false);
    		operacion.getPagosOrigen().remove(pago);
    	}
    	if (operacion.getTipo() == TipoOperacion.VENTA) {
    		cajaService.actualizarSaldoDisponible(origen, pago.getValor(), true);
    		operacion.getPagosOrigen().remove(pago);
    	}
    	
    	pagoService.eliminarPago(pago);
    	operacionRepository.save(operacion);
    	return operacionMapper.toResponse(operacion); 
    }
    
    public OperacionResponse agregarPagoConversion(Long id, PagoDTO pagoRequest) {
    	Operacion operacion = operacionRepository.findById(id)
    			.orElseThrow(() -> new RuntimeException("Operación no encontrada"));;
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
    
    public OperacionResponse quitarPagoConversion(Long id, PagoDTO pagoRequest) {
    	Operacion operacion = operacionRepository.findById(id)
    			.orElseThrow(() -> new RuntimeException("Operación no encontrada"));;
    	Pago pago = pagoMapper.toEntity(pagoRequest);
    	
    	Caja origen = cajaService.obtenerPorMoneda(operacion.getMonedaConversion());
    	if (operacion.getTipo() == TipoOperacion.COMPRA) {
    		cajaService.actualizarSaldoDisponible(origen, pago.getValor(), true);
    		operacion.getPagosConversion().remove(pago);
    	}
    	if (operacion.getTipo() == TipoOperacion.VENTA) {
    		cajaService.actualizarSaldoDisponible(origen, pago.getValor(), false);
    		operacion.getPagosConversion().remove(pago);
    	}
    	
    	pagoService.eliminarPago(pago);
    	operacionRepository.save(operacion);
    	return operacionMapper.toResponse(operacion); 
    }
    
    // Cambiar MonedaOrigen
    
     
    
    
    
    
    // Cambiar MontoOrigen
    
  
   
    
    
    
    // Cambiar MonedaDestino
    
    
    
    //Agregar Referido
    
    
    //Quitar referido
    
   
    // Agregar puntos referido
    
    // Cambiar puntos referido
    
    // Agregar Moneda Referido
    
    //Cambiar moneda referido
            
//    public LocalDateTime parsearFecha(String fecha) {
//    	try {
//    		return (fecha != null) ? LocalDateTime.of(LocalDate.parse(fecha, FORMATTER), LocalTime.MIN) : null;
//    	} catch (DateTimeParseException e) {
//    	    throw new IllegalArgumentException("Formato de fecha incorrecto. Usa dd-MM-yyyy");
//    	}
//    }
    
    public LocalDateTime parsearFecha(String fecha) {
        if (fecha == null) {
            return null;
        }

        try {
            // Verificar si la fecha está en formato yyyy-MM-dd
            if (fecha.matches("\\d{4}-\\d{2}-\\d{2}")) {
                // Convertir de yyyy-MM-dd a dd-MM-yyyy
                String[] partes = fecha.split("-");
                fecha = partes[2] + "-" + partes[1] + "-" + partes[0]; // Reordenar a dd-MM-yyyy
            }

            // Parsear la fecha en el formato esperado (dd-MM-yyyy)
            LocalDate localDate = LocalDate.parse(fecha, FORMATTER);
            return LocalDateTime.of(localDate, LocalTime.MIN);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Formato de fecha incorrecto. Usa dd-MM-yyyy o yyyy-MM-dd");
        }
    }
       
    public boolean existePorCuentaCorriente(Long id) {
    	return operacionRepository.existsByCuentaCorrienteId(id);
    }
        
          
}


