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
import com.thames.finance_app.mappers.MovimientoCtaCteMapper;
import com.thames.finance_app.models.CuentaCorriente;
import com.thames.finance_app.models.MovimientoCtaCte;
import com.thames.finance_app.models.Operacion;
import com.thames.finance_app.models.Caja;
import com.thames.finance_app.repositories.CtaCteRepository;
import com.thames.finance_app.repositories.MovimientoCtaCteRepository;
import com.thames.finance_app.specifications.MovimientoCtaCteSpecification;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MovimientoCtaCteService {
   
    private final MovimientoCtaCteRepository movimientoCtaCteRepository;
    private final MovimientoCtaCteMapper movimientoCtaCteMapper;
    private final CtaCteRepository ctaCteRepository;

    public Page<MovimientoCtaCteDTO> obtenerMovimientosFiltrados(
            String nombreTitular, String tipo, Date fechaDesde, Date fechaHasta, 
            BigDecimal monto, String moneda, Pageable pageable) {

        Specification<MovimientoCtaCte> spec = 
            MovimientoCtaCteSpecification.filtrarMovimientos(nombreTitular, tipo, fechaDesde, fechaHasta, monto, moneda);

        return movimientoCtaCteRepository.findAll(spec, pageable).map(movimientoCtaCteMapper::toDTO);
    }

    public void registrarMovimientos(Operacion operacion, Caja cajaOrigen, Caja cajaConversion) {
    	if (operacion.getTipo() == TipoOperacion.COMPRA) {
    		MovimientoCtaCte ingreso = movimientoCtaCteMapper.toEntity(operacion, TipoMovimiento.EGRESO, cajaOrigen.getMoneda(),
    														operacion.getMontoOrigen());
    		registrarMovimiento(ingreso);
    		
    		MovimientoCtaCte egreso = movimientoCtaCteMapper.toEntity(operacion, TipoMovimiento.INGRESO, cajaConversion.getMoneda(),
    														operacion.getMontoConversion());
    		registrarMovimiento(egreso);
    	} else if (operacion.getTipo() == TipoOperacion.VENTA) {
    		MovimientoCtaCte ingreso = movimientoCtaCteMapper.toEntity(operacion, TipoMovimiento.INGRESO, cajaOrigen.getMoneda(),
															operacion.getMontoOrigen());
    		registrarMovimiento(ingreso);

    		MovimientoCtaCte egreso = movimientoCtaCteMapper.toEntity(operacion, TipoMovimiento.EGRESO, cajaConversion.getMoneda(),
    														operacion.getMontoConversion());
    		registrarMovimiento(egreso);    		
    		
    	}
    }
    
    public void registrarMovimientoReferido(Operacion operacion) {
    	MovimientoCtaCte ganancia = movimientoCtaCteMapper.toEntity(operacion, TipoMovimiento.INGRESO, 
    										operacion.getMonedaReferido(), operacion.getGananciaReferido(operacion.getMontoOrigen()));
    	registrarMovimiento(ganancia);
    }
    
    public void revertirRegistroMovimientoReferido(Operacion operacion) {
    	MovimientoCtaCte ganancia = movimientoCtaCteMapper.toEntity(operacion, TipoMovimiento.EGRESO, 
    										operacion.getMonedaReferido(), operacion.getGananciaReferido(operacion.getMontoOrigen()));
    	registrarMovimiento(ganancia);
    }
    
  
    private void registrarMovimiento (MovimientoCtaCte movimiento) {
    	movimientoCtaCteRepository.save(movimiento);  	
    	CuentaCorriente cuenta = movimiento.getCuentaCorriente();
    	cuenta.getMovimientos().add(movimiento);
    	ctaCteRepository.save(cuenta);
    }
    
	
}

