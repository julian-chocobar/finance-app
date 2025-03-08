package com.thames.finance_app.services;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.thames.finance_app.enums.Moneda;
import com.thames.finance_app.enums.TipoMovimiento;
import com.thames.finance_app.enums.TipoOperacion;
import com.thames.finance_app.models.CuentaCorriente;
import com.thames.finance_app.models.MovimientoCtaCte;
import com.thames.finance_app.models.Operacion;
import com.thames.finance_app.models.Caja;
import com.thames.finance_app.repositories.CtaCteRepository;
import com.thames.finance_app.repositories.MovimientoCtaCteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MovimientoCtaCteService {
   
    private final MovimientoCtaCteRepository movimientoCtaCteRepository;
    private final CtaCteRepository ctaCteRepository;

    
    public void registrarMovimientos(Operacion operacion, Caja cajaOrigen, Caja cajaConversion) {
    	if (operacion.getTipo() == TipoOperacion.COMPRA) {
    		MovimientoCtaCte ingreso = toMovimientoCtaCte (operacion, TipoMovimiento.EGRESO, cajaOrigen.getMoneda(),
    														operacion.getMontoOrigen());
    		registrarMovimiento(ingreso);
    		
    		MovimientoCtaCte egreso = toMovimientoCtaCte(operacion, TipoMovimiento.INGRESO, cajaConversion.getMoneda(),
    														operacion.getMontoConversion());
    		registrarMovimiento(egreso);
    	} else if (operacion.getTipo() == TipoOperacion.VENTA) {
    		MovimientoCtaCte ingreso = toMovimientoCtaCte (operacion, TipoMovimiento.INGRESO, cajaOrigen.getMoneda(),
															operacion.getMontoOrigen());
    		registrarMovimiento(ingreso);

    		MovimientoCtaCte egreso = toMovimientoCtaCte(operacion, TipoMovimiento.EGRESO, cajaConversion.getMoneda(),
    														operacion.getMontoConversion());
    		registrarMovimiento(egreso);    		
    		
    	}
    }
    
    public void registrarMovimientoReferido(Operacion operacion) {
    	MovimientoCtaCte ganancia = toMovimientoCtaCte(operacion, TipoMovimiento.INGRESO, 
    										operacion.getMonedaReferido(), operacion.getGananciaReferido());
    	registrarMovimiento(ganancia);
    }
    
    public void revertirRegistroMovimientoReferido(Operacion operacion) {
    	MovimientoCtaCte ganancia = toMovimientoCtaCte(operacion, TipoMovimiento.EGRESO, 
    										operacion.getMonedaReferido(), operacion.getGananciaReferido());
    	registrarMovimiento(ganancia);
    }
    
    
    
    private MovimientoCtaCte toMovimientoCtaCte (Operacion operacion, TipoMovimiento tipo,
    											Moneda moneda, BigDecimal monto) {
    	return MovimientoCtaCte.builder()
    							.fecha(operacion.getFecha())
    							.tipoMovimiento(tipo)
    							.cuentaCorriente(operacion.getCuentaCorriente())
    							.operacion(operacion)
    							.moneda(moneda)
    							.monto(monto)
    							.build();
    	
    }
    
    private void registrarMovimiento (MovimientoCtaCte movimiento) {
    	movimientoCtaCteRepository.save(movimiento);  	
    	CuentaCorriente cuenta = movimiento.getCuentaCorriente();
    	cuenta.getMovimientos().add(movimiento);
    	ctaCteRepository.save(cuenta);
    }
    
    
    
//  private void actualizarSaldos(CuentaCorriente cuenta, Caja caja, TipoMovimiento tipo, Moneda moneda, BigDecimal monto) {
//  if (tipo == TipoMovimiento.INGRESO) {
//      modificarSaldos(cuenta, caja, moneda, monto, true);
//  } else if (tipo == TipoMovimiento.EGRESO) {
//      modificarSaldos(cuenta, caja, moneda, monto, false);
//  }
//}

//private void modificarSaldos(CuentaCorriente cuenta, Caja caja, Moneda moneda, BigDecimal monto, boolean esIngreso) {
//  BigDecimal saldoCuenta = cuenta.getSaldoPorMoneda(moneda);
//  BigDecimal saldoCajaReal = caja.getSaldoReal();
//  BigDecimal saldoCajaDisponible = caja.getSaldoDisponible();
//
//  BigDecimal nuevoSaldoCuenta = esIngreso ? saldoCuenta.add(monto) : saldoCuenta.subtract(monto);
//  BigDecimal nuevoSaldoCajaReal = esIngreso ? saldoCajaReal.add(monto) : saldoCajaReal.subtract(monto);
//  BigDecimal nuevoSaldoCajaDisponible = esIngreso ? saldoCajaDisponible.add(monto) : saldoCajaDisponible.subtract(monto);
//
//  cuenta.actualizarSaldo( moneda, nuevoSaldoCuenta);
//  caja.setSaldoReal(nuevoSaldoCajaReal);
//  caja.setSaldoDisponible(nuevoSaldoCajaDisponible);
//}
	
}

