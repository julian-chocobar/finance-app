package com.thames.finance_app.services;
 
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.thames.finance_app.dtos.OperacionRequest;
import com.thames.finance_app.dtos.OperacionResponse;
import com.thames.finance_app.enums.Moneda;
import com.thames.finance_app.enums.TipoMovimiento;
import com.thames.finance_app.mappers.OperacionMapper;
import com.thames.finance_app.models.CuentaCorriente;
import com.thames.finance_app.models.Liquidador;
import com.thames.finance_app.models.Operacion;
import com.thames.finance_app.repositories.OperacionRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OperacionService {
	
    private final OperacionRepository operacionRepository;
    private final CtaCteService ctaCteService;
    private final LiquidadorService liquidadorService;
    private final OperacionMapper operacionMapper;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    @Transactional
    public OperacionResponse crearOperacion(OperacionRequest request) {
        CuentaCorriente cuentaCliente = ctaCteService.obtenerCuentaPorId(request.getCuentaCorrienteId());
        CuentaCorriente cuentaReferido = Optional.ofNullable(request.getCuentaCorrienteReferidoId())
                .map(ctaCteService::obtenerCuentaPorId)
                .orElse(null);

        Liquidador liquidador = request.getLiquidadorId() != null
                ? liquidadorService.obtenerPorId(request.getLiquidadorId())
                : null;

        Operacion operacion = Operacion.builder()
                .fecha(LocalDateTime.now())
                .tipo(request.getTipo())
                .cuentaCorriente(cuentaCliente)
                .monedaOrigen(request.getMonedaOrigen())
                .montoOrigen(request.getMontoOrigen())
                .tipoCambio(request.getTipoCambio())
                .monedaConversion(request.getMonedaConversion())
                .montoConversion(request.getMontoConversion())
                .estado(request.getEstado())
                .tipoEntrega(request.getTipoEntrega())
                .cuentaCorrienteReferido(cuentaReferido)
                .puntosReferido(request.getPuntosReferido())
                .monedaReferido(request.getMonedaReferido())
                .gananciaReferido(calcularGananciaReferido(request))
                .liquidador(liquidador)
                .montoLiquidador(request.getMontoLiquidador())
                .build();

        operacion = operacionRepository.save(operacion);
        actualizarSaldoReferido(operacion);
        actualizarSaldosCajas(operacion);
       
        if (cuentaCliente.getMovimientos() == null) {
            cuentaCliente.setMovimientos(new ArrayList<>());
        }
        cuentaCliente.getMovimientos().add(operacion);
        return operacionMapper.toResponse(operacion);
    }
    
    private void actualizarSaldoReferido(Operacion operacion) {
        CuentaCorriente cuentaReferido = operacion.getCuentaCorrienteReferido();
        if (cuentaReferido == null || operacion.getGananciaReferido() == null || operacion.getGananciaReferido().compareTo(BigDecimal.ZERO) == 0) {
            return;
        }

        Moneda monedaReferido = operacion.getMonedaReferido();
        BigDecimal saldoActual = cuentaReferido.getSaldoPorMoneda(monedaReferido);
        cuentaReferido.setSaldoPorMoneda(monedaReferido, saldoActual.add(operacion.getGananciaReferido()));
    }
    
    private BigDecimal calcularGananciaReferido(OperacionRequest request) {
        if (request.getCuentaCorrienteReferidoId() != null && request.getPuntosReferido() != null) {
            return request.getPuntosReferido().multiply(request.getMontoOrigen());
        }
        return BigDecimal.ZERO;
    }

    private void actualizarSaldosCajas(Operacion operacion) {
    	//Identifica las cajas afectadas según monedaOrigen y monedaDestino.
    	
    	//Llama a CajaService.actualizarSaldoReal() para reflejar el compromiso de fondos.
    	
    	//Llama a CajaService.actualizarSaldoDisponible() cuando la operación se ejecuta.
    }
    
    public void confirmarEjecucionOperacion(Long operacionId, BigDecimal montoOrigenEjecutado, BigDecimal montoConversionEjecutado) {
    	//Marca la operacion como ejecutada
    	
    	//Ajusta los saldos disponibles
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
        Operacion operacion = operacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Operación no encontrada"));
        
        CuentaCorriente cuentaCliente = ctaCteService.obtenerCuentaPorId(request.getCuentaCorrienteId());
        
        CuentaCorriente cuentaReferido = request.getCuentaCorrienteReferidoId() != null 
        	    ? ctaCteService.obtenerCuentaPorId(request.getCuentaCorrienteReferidoId()) 
        	    : null;

        Liquidador liquidador = request.getLiquidadorId() != null 
        	    ? liquidadorService.obtenerPorId(request.getLiquidadorId()) 
        	    : null;

        operacionMapper.updateEntity(operacion, request,  liquidador, cuentaCliente, cuentaReferido );
        operacion = operacionRepository.save(operacion);
        actualizarSaldosCuentaCorriente(operacion);
        return operacionMapper.toResponse(operacion);
    }
    
    private void actualizarSaldosCuentaCorriente(Operacion operacion) {
        if (operacion.getCuentaCorriente() != null) {
            ctaCteService.actualizarSaldos(
                    operacion.getCuentaCorriente().getId(),
                    operacion.getMontoOrigen(),
                    operacion.getMonedaOrigen(),
                    TipoMovimiento.EGRESO
            );

            ctaCteService.actualizarSaldos(
                    operacion.getCuentaCorriente().getId(),
                    operacion.getMontoConversion(),
                    operacion.getMonedaConversion(),
                    TipoMovimiento.INGRESO
            );
        }

        // Actualizar saldo del referido si tiene ganancia
        if (operacion.getCuentaCorrienteReferido() != null && operacion.getGananciaReferido() != null) {
            ctaCteService.actualizarSaldos(
                    operacion.getCuentaCorrienteReferido().getId(),
                    operacion.getGananciaReferido(),
                    operacion.getMonedaReferido(),
                    TipoMovimiento.INGRESO
            );
        }
    }
    
    public LocalDateTime parsearFecha(String fecha) {
    	try {
    		return (fecha != null) ? LocalDateTime.of(LocalDate.parse(fecha, FORMATTER), LocalTime.MIN) : null;
    	} catch (DateTimeParseException e) {
    	    throw new IllegalArgumentException("Formato de fecha incorrecto. Usa dd-MM-yyyy");
    	}
    }
    
    
   
}


