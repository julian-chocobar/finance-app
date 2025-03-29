package com.thames.finance_app.services;

import java.math.BigDecimal;
import java.sql.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.thames.finance_app.dtos.MovimientoCtaCteDTO;
import com.thames.finance_app.enums.TipoMovimiento;
import com.thames.finance_app.enums.TipoOperacion;
import com.thames.finance_app.mappers.CtaCteMapper;
import com.thames.finance_app.models.Caja;
import com.thames.finance_app.models.CuentaCorriente;
import com.thames.finance_app.models.MovimientoCtaCte;
import com.thames.finance_app.models.Operacion;
import com.thames.finance_app.repositories.CtaCteRepository;
import com.thames.finance_app.repositories.MovimientoCtaCteRepository;
import com.thames.finance_app.specifications.MovimientoCtaCteSpecification;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MovimientoCtaCteService {

    private final MovimientoCtaCteRepository movimientoCtaCteRepository;
    private final CtaCteMapper ctaCteMapper;
    private final CtaCteRepository ctaCteRepository;

    public Page<MovimientoCtaCteDTO> obtenerMovimientosFiltrados(
            String nombreTitular, String tipo, Date fechaDesde, Date fechaHasta,
            BigDecimal monto, String moneda, Pageable pageable) {

        Specification<MovimientoCtaCte> spec =
            MovimientoCtaCteSpecification.filtrarMovimientos(nombreTitular, tipo, fechaDesde, fechaHasta, monto, moneda);

        return movimientoCtaCteRepository.findAll(spec, pageable).map(ctaCteMapper::toMovimientoDTO);
    }

    public void registrarMovimientosOperacion(Operacion operacion, Caja cajaOrigen, Caja cajaConversion) {
    	if (operacion.getTipo() == TipoOperacion.COMPRA) {
    		MovimientoCtaCte ingreso = ctaCteMapper.toMovimientoEntity(operacion, TipoMovimiento.EGRESO, cajaOrigen.getMoneda(),
    														operacion.getMontoOrigen());
    		registrarMovimiento(ingreso);

    		MovimientoCtaCte egreso = ctaCteMapper.toMovimientoEntity(operacion, TipoMovimiento.INGRESO, cajaConversion.getMoneda(),
    														operacion.getMontoConversion());
    		registrarMovimiento(egreso);
    	} else if (operacion.getTipo() == TipoOperacion.VENTA) {
    		MovimientoCtaCte ingreso = ctaCteMapper.toMovimientoEntity(operacion, TipoMovimiento.INGRESO, cajaOrigen.getMoneda(),
															operacion.getMontoOrigen());
    		registrarMovimiento(ingreso);

    		MovimientoCtaCte egreso = ctaCteMapper.toMovimientoEntity(operacion, TipoMovimiento.EGRESO, cajaConversion.getMoneda(),
    														operacion.getMontoConversion());
    		registrarMovimiento(egreso);

    	}
    }

    public void impactoMovimiento(MovimientoCtaCte movimiento) {
    	CuentaCorriente cuenta = movimiento.getCuentaCorriente();
	    BigDecimal saldoActual = cuenta.getSaldoPorMoneda(movimiento.getMoneda());
    	if (movimiento.getTipoMovimiento() == TipoMovimiento.INGRESO) {
    		cuenta.actualizarSaldo(movimiento.getMoneda(), saldoActual.add(movimiento.getMonto()));
    	} else if (movimiento.getTipoMovimiento() == TipoMovimiento.EGRESO) {
    		cuenta.actualizarSaldo(movimiento.getMoneda(), saldoActual.subtract(movimiento.getMonto()));
        } else {
	        throw new IllegalArgumentException("Tipo de movimiento no válido para actualización de saldo.");
	    }
    	registrarMovimiento(movimiento);
    }

    public void registrarMovimientoReferido(Operacion operacion) {
    	MovimientoCtaCte ganancia = ctaCteMapper.toMovimientoEntity(operacion, TipoMovimiento.INGRESO,
    										operacion.getMonedaReferido(), operacion.getGananciaReferido());
    	registrarMovimiento(ganancia);
    }

    public void revertirRegistroMovimientoReferido(Operacion operacion) {
    	MovimientoCtaCte ganancia = ctaCteMapper.toMovimientoEntity(operacion, TipoMovimiento.EGRESO,
    										operacion.getMonedaReferido(), operacion.getGananciaReferido());
    	registrarMovimiento(ganancia);
    }


    private void registrarMovimiento (MovimientoCtaCte movimiento) {
    	movimientoCtaCteRepository.save(movimiento);
    	CuentaCorriente cuenta = movimiento.getCuentaCorriente();
    	cuenta.getMovimientos().add(movimiento);
    	ctaCteRepository.save(cuenta);
    }


}

