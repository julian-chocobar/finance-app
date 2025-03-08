package com.thames.finance_app.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.thames.finance_app.dtos.CtaCteRequest;
import com.thames.finance_app.dtos.CtaCteResponse;
import com.thames.finance_app.dtos.MovimientoCtaCteResponse;
import com.thames.finance_app.enums.Moneda;
import com.thames.finance_app.enums.TipoMovimiento;
import com.thames.finance_app.enums.TipoOperacion;
import com.thames.finance_app.exceptions.BusinessException;
import com.thames.finance_app.mappers.CtaCteMapper;
import com.thames.finance_app.models.Caja;
import com.thames.finance_app.models.Cliente;
import com.thames.finance_app.models.CuentaCorriente;
import com.thames.finance_app.models.MovimientoCtaCte;
import com.thames.finance_app.models.Operacion;
import com.thames.finance_app.repositories.CtaCteRepository;
import com.thames.finance_app.repositories.MovimientoCtaCteRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CtaCteService {

    private final CtaCteRepository ctaCteRepository;
    private final MovimientoCtaCteRepository movimientoCtaCteRepository;
    private final ClienteService clienteService;
    private final CajaService cajaService;
    private final MovimientoCtaCteService movimientoCtaCteService;
    private final CtaCteMapper ctaCteMapper;

    public CtaCteResponse obtenerResponsePorId(Long cuentaId) {
        CuentaCorriente cuenta = ctaCteRepository.findById(cuentaId)
                .orElseThrow(() -> new BusinessException("Cuenta Corriente no encontrada"));
        return ctaCteMapper.toResponse(cuenta);
    }
    
    public CuentaCorriente obtenerCuentaPorId(Long cuentaId) {
        CuentaCorriente cuenta = ctaCteRepository.findById(cuentaId)
                .orElseThrow(() -> new BusinessException("Cuenta Corriente no encontrada"));
        return cuenta;
    }

    public CtaCteResponse obtenerResponsePorClienteId(Long clienteId) {
        CuentaCorriente cuenta = ctaCteRepository.findByClienteId(clienteId)
                .orElseThrow(() -> new BusinessException("Cuenta Corriente no encontrada para el cliente"));
        return ctaCteMapper.toResponse(cuenta);
    }
    
    public CuentaCorriente obtenerEntidadPorClienteId(Long clienteId) {
        return ctaCteRepository.findByClienteId(clienteId)
                .orElseThrow(() -> new BusinessException("Cuenta Corriente no encontrada para el cliente"));
    }
 
    @Transactional
    public CtaCteResponse crearCuentaCorriente(CtaCteRequest request) {
    	Cliente cliente = clienteService.obtenerEntidadPorID(request.getClienteId());
    	 
        if (ctaCteRepository.existsByClienteId(cliente.getId())) {
            throw new BusinessException("El cliente ya tiene una cuenta corriente");
        }

        CuentaCorriente cuentaCorriente = new CuentaCorriente();
        cuentaCorriente.setCliente(cliente);
        ctaCteRepository.save(cuentaCorriente);

        return ctaCteMapper.toResponse2(cuentaCorriente, Page.empty());
    }


    public void eliminarCuenta(Long cuentaId) {
        CuentaCorriente cuenta = ctaCteRepository.findById(cuentaId)
                .orElseThrow(() -> new BusinessException("Cuenta Corriente no encontrada"));
        ctaCteRepository.delete(cuenta);
    }
    

    public Page<MovimientoCtaCteResponse> obtenerMovimientos(Long cuentaId, LocalDate fechaDesde, LocalDate fechaHasta,
            LocalDate fechaExacta, BigDecimal monto,
            Moneda moneda, TipoMovimiento tipo,
            Pageable pageable) {
		Page<MovimientoCtaCte> movimientos = filtrarMovimientos(cuentaId, fechaExacta, fechaDesde, fechaHasta, monto, moneda, tipo, pageable);
		return movimientos.map(ctaCteMapper::toMovimientoResponse);
	}
    
    public BigDecimal obtenerSaldoPorMoneda(CuentaCorriente cuenta, Moneda moneda) {
    	return cuenta.getSaldoPorMoneda(moneda);
    }

    public void setSaldoPorMoneda(CuentaCorriente cuenta, BigDecimal nuevoSaldo, Moneda moneda) {
      cuenta.setSaldoPorMoneda(moneda, nuevoSaldo);
    }
    
    public Page<MovimientoCtaCte> filtrarMovimientos(
            Long cuentaCorrienteId,
            LocalDate fechaExacta,
            LocalDate fechaDesde,
            LocalDate fechaHasta,
            BigDecimal monto,
            Moneda moneda,
            TipoMovimiento tipoMovimiento,
            Pageable pageable) {
            return movimientoCtaCteRepository.filtrarMovimientos(
                cuentaCorrienteId, fechaExacta, fechaDesde, fechaHasta, monto, moneda, tipoMovimiento, pageable);
    }
	
	public void impactoOperacion(Operacion operacion) {	
		CuentaCorriente cuentaCliente = operacion.getCuentaCorriente();
		
	    BigDecimal saldoActualOrigen = cuentaCliente.getSaldoPorMoneda(operacion.getMonedaOrigen());
	    BigDecimal saldoActualConversion = cuentaCliente.getSaldoPorMoneda(operacion.getMonedaConversion());

	    if (operacion.getTipo() == TipoOperacion.COMPRA) {
	        // El cliente vende la monedaOrigen a la empresa y recibe monedaConversion
	        cuentaCliente.actualizarSaldo(operacion.getMonedaOrigen(), saldoActualOrigen.subtract(operacion.getTotalPagosOrigen())); // Cliente entrega monedaOrigen
	        cuentaCliente.actualizarSaldo(operacion.getMonedaConversion(), saldoActualConversion.add(operacion.getTotalPagosConversion())); // Cliente recibe monedaConversion
	    } else if (operacion.getTipo() == TipoOperacion.VENTA) {
	        // El cliente compra la monedaConversion a la empresa y entrega monedaOrigen
	        cuentaCliente.actualizarSaldo(operacion.getMonedaOrigen(), saldoActualOrigen.add(operacion.getTotalPagosOrigen())); // Cliente recibe monedaOrigen
	        cuentaCliente.actualizarSaldo(operacion.getMonedaConversion(), saldoActualConversion.subtract(operacion.getTotalPagosConversion())); // Cliente entrega monedaConversion
	    } else {
	        throw new IllegalArgumentException("Tipo de operación no válido para actualización de saldo.");
	    }
	    
	    Caja origen = cajaService.obtenerPorMoneda(operacion.getMonedaOrigen());
	    Caja conversion = 	cajaService.obtenerPorMoneda(operacion.getMonedaConversion());	
	    movimientoCtaCteService.registrarMovimientos(operacion, origen, conversion);
	    ctaCteRepository.save(cuentaCliente);
	}
	
	@Transactional
	public void revertirImpactoOperacion(Operacion operacion) {
	    CuentaCorriente cuentaCliente = operacion.getCuentaCorriente();
	    
	    if (cuentaCliente == null) {
	        throw new BusinessException("La operación no tiene una cuenta corriente asociada");
	    }
	    List<MovimientoCtaCte> movimientos = movimientoCtaCteRepository.findByOperacion(operacion);
	    
	    BigDecimal saldoActualOrigen = cuentaCliente.getSaldoPorMoneda(operacion.getMonedaOrigen());
	    BigDecimal saldoActualConversion = cuentaCliente.getSaldoPorMoneda(operacion.getMonedaConversion());
	    
	    if (operacion.getTipo() == TipoOperacion.COMPRA) {
	        cuentaCliente.actualizarSaldo(operacion.getMonedaOrigen(), saldoActualOrigen.add(operacion.getTotalPagosOrigen()));
	        cuentaCliente.actualizarSaldo(operacion.getMonedaConversion(), saldoActualConversion.subtract(operacion.getTotalPagosConversion()));
	    } else if (operacion.getTipo() == TipoOperacion.VENTA) {
	        cuentaCliente.actualizarSaldo(operacion.getMonedaOrigen(), saldoActualOrigen.subtract(operacion.getTotalPagosOrigen()));
	        cuentaCliente.actualizarSaldo(operacion.getMonedaConversion(), saldoActualConversion.add(operacion.getTotalPagosConversion()));
	    }    
	    movimientoCtaCteRepository.deleteAll(movimientos);
	    ctaCteRepository.save(cuentaCliente);
	}	
	
    public void impactoOperacionReferido(Operacion operacion) {
    	CuentaCorriente cuentaReferido = operacion.getCuentaCorrienteReferido();
        if (cuentaReferido == null || operacion.getGananciaReferido() == null 
        	|| operacion.getGananciaReferido().compareTo(BigDecimal.ZERO) == 0) {
            return;
        }   
        Moneda monedaReferido = operacion.getMonedaReferido();
        BigDecimal saldoActual = cuentaReferido.getSaldoPorMoneda(monedaReferido);
        cuentaReferido.setSaldoPorMoneda(monedaReferido, saldoActual.add(operacion.getGananciaReferido()));
	    
        movimientoCtaCteService.registrarMovimientoReferido(operacion);
        ctaCteRepository.save(cuentaReferido);       
    }
    
    public void revertirImpactoOperacionReferido (Operacion operacion) {
    	CuentaCorriente cuentaReferido = operacion.getCuentaCorrienteReferido();
        if (cuentaReferido == null || operacion.getGananciaReferido() == null 
        	|| operacion.getGananciaReferido().compareTo(BigDecimal.ZERO) == 0) {
            return;
        }        
        Moneda monedaReferido = operacion.getMonedaReferido();
        BigDecimal saldoActual = cuentaReferido.getSaldoPorMoneda(monedaReferido);
        cuentaReferido.setSaldoPorMoneda(monedaReferido, saldoActual.subtract(operacion.getGananciaReferido()));
	    
        movimientoCtaCteService.revertirRegistroMovimientoReferido(operacion);
        ctaCteRepository.save(cuentaReferido);
    }

	public void actualizarPorCambioOperacion(Operacion vieja, Operacion nueva) {
		revertirImpactoOperacion(vieja);
		impactoOperacion(nueva);		
	}
   
}

