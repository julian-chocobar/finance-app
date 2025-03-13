package com.thames.finance_app.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.thames.finance_app.dtos.CtaCteResponse;
import com.thames.finance_app.dtos.MovimientoCtaCteDTO;
import com.thames.finance_app.enums.TipoMovimiento;
import com.thames.finance_app.enums.TipoOperacion;
import com.thames.finance_app.exceptions.BusinessException;
import com.thames.finance_app.mappers.CtaCteMapper;
import com.thames.finance_app.models.Caja;
import com.thames.finance_app.models.CuentaCorriente;
import com.thames.finance_app.models.Moneda;
import com.thames.finance_app.models.MovimientoCtaCte;
import com.thames.finance_app.models.Operacion;
import com.thames.finance_app.repositories.CtaCteRepository;
import com.thames.finance_app.repositories.MovimientoCtaCteRepository;
import com.thames.finance_app.repositories.OperacionRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CtaCteService {

    private final CtaCteRepository ctaCteRepository;
    private final MovimientoCtaCteRepository movimientoCtaCteRepository;
	private final OperacionRepository operacionRepository;
    private final CajaService cajaService;
    private final MovimientoCtaCteService movimientoCtaCteService;
    private final CtaCteMapper ctaCteMapper;
    
    public Page<MovimientoCtaCte> listarMovimientos(Pageable pageable) {
        return movimientoCtaCteRepository.findAllOrderByFechaDesc(pageable);
    }

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
 
    public void eliminarCuenta(Long cuentaId) {
        CuentaCorriente cuenta = ctaCteRepository.findById(cuentaId)
                .orElseThrow(() -> new BusinessException("Cuenta Corriente no encontrada"));
        if (operacionRepository.existsByCuentaCorriente(cuenta)) {
            throw new RuntimeException("No se puede eliminar la cuenta porque está asociada a operaciones.");
        }
     // Verificar que todos los saldos de la cuenta estén en 0
        boolean tieneSaldosPendientes = cuenta.getSaldos().values().stream()
                .anyMatch(saldo -> saldo.compareTo(BigDecimal.ZERO) != 0);

        if (tieneSaldosPendientes) {
            throw new BusinessException("No se puede eliminar la cuenta porque tiene saldos pendientes en una o más monedas.");
        }
        ctaCteRepository.delete(cuenta);
    }
    

    public Page<MovimientoCtaCteDTO> obtenerMovimientos(Long cuentaId, LocalDate fechaDesde, LocalDate fechaHasta,
            LocalDate fechaExacta, BigDecimal monto,
            Moneda moneda, TipoMovimiento tipo,
            Pageable pageable) {
		Page<MovimientoCtaCte> movimientos = filtrarMovimientos(cuentaId, fechaExacta, fechaDesde, fechaHasta, monto, moneda, tipo, pageable);
		return movimientos.map(ctaCteMapper::toMovimientoResponse);
	}
    
    public Page<MovimientoCtaCteDTO> obtenerTodosLosMovimientos(
            LocalDate fechaExacta,
            LocalDate fechaDesde,
            LocalDate fechaHasta,
            BigDecimal monto,
            Moneda moneda,
            TipoMovimiento tipo,
            Pageable pageable) {
        
        Page<MovimientoCtaCte> movimientos = filtrarTodosLosMovimientos(
            fechaExacta, fechaDesde, fechaHasta, monto, moneda, tipo, pageable);
        
        return movimientos.map(ctaCteMapper::toMovimientoResponse);
    }
    
    public Page<MovimientoCtaCte> filtrarTodosLosMovimientos(
            LocalDate fechaExacta,
            LocalDate fechaDesde,
            LocalDate fechaHasta,
            BigDecimal monto,
            Moneda moneda,
            TipoMovimiento tipoMovimiento,
            Pageable pageable) {
        
        return movimientoCtaCteRepository.filtrarMovimientos(
            fechaExacta, fechaDesde, fechaHasta, monto, moneda, tipoMovimiento, pageable);
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
        if (cuentaReferido == null || operacion.getGananciaReferido(operacion.getMontoOrigen()) == null 
        	|| operacion.getGananciaReferido(operacion.getMontoOrigen()).compareTo(BigDecimal.ZERO) == 0) {
            return;
        }   
        Moneda monedaReferido = operacion.getMonedaReferido();
        BigDecimal saldoActual = cuentaReferido.getSaldoPorMoneda(monedaReferido);
        cuentaReferido.setSaldoPorMoneda(monedaReferido, saldoActual.add(operacion.getGananciaReferido(operacion.getMontoOrigen())));
	    
        movimientoCtaCteService.registrarMovimientoReferido(operacion);
        ctaCteRepository.save(cuentaReferido);       
    }
    
    public void revertirImpactoOperacionReferido (Operacion operacion) {
    	CuentaCorriente cuentaReferido = operacion.getCuentaCorrienteReferido();
        if (cuentaReferido == null || operacion.getGananciaReferido(operacion.getMontoOrigen()) == null 
        	|| operacion.getGananciaReferido(operacion.getMontoOrigen()).compareTo(BigDecimal.ZERO) == 0) {
            return;
        }        
        Moneda monedaReferido = operacion.getMonedaReferido();
        BigDecimal saldoActual = cuentaReferido.getSaldoPorMoneda(monedaReferido);
        cuentaReferido.setSaldoPorMoneda(monedaReferido, saldoActual.subtract(operacion.getGananciaReferido(operacion.getMontoOrigen())));
	    
        movimientoCtaCteService.revertirRegistroMovimientoReferido(operacion);
        ctaCteRepository.save(cuentaReferido);
    }

	public CtaCteResponse actualizarSaldos(Long id, BigDecimal monto, Moneda moneda, TipoMovimiento tipo) {
		CuentaCorriente cuenta = ctaCteRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Cuenta Corriente no encontrada"));
	    BigDecimal saldoActual = cuenta.getSaldoPorMoneda(moneda);
	    if (tipo == TipoMovimiento.INGRESO) {
		    cuenta.setSaldoPorMoneda(moneda, saldoActual.add(monto));

	    }else {
		    cuenta.setSaldoPorMoneda(moneda, saldoActual.subtract(monto));
	    }
	    ctaCteRepository.save(cuenta);
	    return ctaCteMapper.toResponse(cuenta);	
	    		
	}

   
}

