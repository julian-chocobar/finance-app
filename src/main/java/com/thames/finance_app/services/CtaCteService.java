package com.thames.finance_app.services;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.thames.finance_app.dtos.CtaCteResponse;
import com.thames.finance_app.enums.TipoMovimiento;
import com.thames.finance_app.enums.TipoOperacion;
import com.thames.finance_app.exceptions.BusinessException;
import com.thames.finance_app.mappers.CtaCteMapper;
import com.thames.finance_app.models.Caja;
import com.thames.finance_app.models.CuentaCorriente;
import com.thames.finance_app.models.Moneda;
import com.thames.finance_app.models.MovimientoCtaCte;
import com.thames.finance_app.models.Operacion;
import com.thames.finance_app.models.Titular;
import com.thames.finance_app.repositories.CtaCteRepository;
import com.thames.finance_app.repositories.MovimientoCtaCteRepository;
import com.thames.finance_app.repositories.OperacionRepository;
import com.thames.finance_app.repositories.TitularRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CtaCteService {

    private final CtaCteRepository ctaCteRepository;
    private final MovimientoCtaCteRepository movimientoCtaCteRepository;
	private final OperacionRepository operacionRepository;
	private final TitularRepository titularRepository;
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

    public CtaCteResponse obtenerResponsePorTitularId(Long titularId) {
    	Titular titular = titularRepository.findById(titularId)
    			.orElseThrow(() -> new BusinessException("Cliente no encontrado"));
        CuentaCorriente cuenta = ctaCteRepository.findByTitular(titular)
                .orElseThrow(() -> new BusinessException("Cuenta Corriente no encontrada para el cliente"));
        return ctaCteMapper.toResponse(cuenta);
    }
    
    public CuentaCorriente obtenerEntidadPorTitularId(Long titularId) {
      	Titular titular = titularRepository.findById(titularId)
    			.orElseThrow(() -> new BusinessException("Cliente no encontrado"));
        return ctaCteRepository.findByTitular(titular)
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
	
    public BigDecimal obtenerSaldoPorMoneda(CuentaCorriente cuenta, Moneda moneda) {
    	return cuenta.getSaldoPorMoneda(moneda);
    }

    public void setSaldoPorMoneda(CuentaCorriente cuenta, BigDecimal nuevoSaldo, Moneda moneda) {
      cuenta.setSaldoPorMoneda(moneda, nuevoSaldo);
    }

   
}

