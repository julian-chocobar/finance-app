package com.thames.finance_app.services;

import java.math.BigDecimal;
import java.sql.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.thames.finance_app.dtos.MovimientoCajaDTO;
import com.thames.finance_app.enums.TipoMovimiento;
import com.thames.finance_app.enums.TipoOperacion;
import com.thames.finance_app.mappers.CajaMapper;
import com.thames.finance_app.models.Caja;
import com.thames.finance_app.models.MovimientoCaja;
import com.thames.finance_app.models.Operacion;
import com.thames.finance_app.repositories.CajaRepository;
import com.thames.finance_app.repositories.MovimientoCajaRepository;
import com.thames.finance_app.specifications.MovimientoCajaSpecification;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MovimientoCajaService {
	
	private final MovimientoCajaRepository movimientoCajaRepository;
	private final CajaRepository cajaRepository;
	private final CajaMapper cajaMapper;
	
	
	public Page<MovimientoCajaDTO> obtenerMovimientosFiltrados(String nombreCaja, String tipo, Date fechaInicio,
			Date fechaFin, BigDecimal monto, String moneda, Pageable pageable) {
		
		Specification <MovimientoCaja> spec =
				MovimientoCajaSpecification.filtrarMovimientos(nombreCaja,tipo, fechaInicio, fechaFin, monto, moneda);
		return movimientoCajaRepository.findAll(spec, pageable).map(cajaMapper::toMovimientoDTO);
	}
	
    public void registrarMovimientos(Operacion operacion, Caja cajaOrigen, Caja cajaConversion) {
        if (operacion.getTipo() == TipoOperacion.COMPRA) {
            // En una compra, la caja de ORIGEN recibe la moneda (INGRESO)
            MovimientoCaja ingreso = toMovimientoCaja(operacion, TipoMovimiento.INGRESO, cajaOrigen, 
                                                      operacion.getMontoOrigen(), operacion.getTotalPagosOrigen());
            registrarMovimiento(ingreso);

            // La caja de CONVERSIÓN entrega la moneda pagada (EGRESO)
            MovimientoCaja egreso = toMovimientoCaja(operacion, TipoMovimiento.EGRESO, cajaConversion, 
                                                     operacion.getMontoConversion(), operacion.getTotalPagosConversion());
            registrarMovimiento(egreso);
        } else if (operacion.getTipo() == TipoOperacion.VENTA) {
            // En una venta, la caja de ORIGEN entrega la moneda vendida (EGRESO)
            MovimientoCaja egreso = toMovimientoCaja(operacion, TipoMovimiento.EGRESO, cajaOrigen, 
                                                     operacion.getMontoOrigen(), operacion.getTotalPagosOrigen());
            registrarMovimiento(egreso);

            // La caja de CONVERSIÓN recibe la moneda pagada (INGRESO)
            MovimientoCaja ingreso = toMovimientoCaja(operacion, TipoMovimiento.INGRESO, cajaConversion, 
                                                      operacion.getMontoConversion(), operacion.getTotalPagosConversion());
            registrarMovimiento(ingreso);
        }
    }
    
	
	private void registrarMovimiento(MovimientoCaja movimiento) {
	        // Guardamos el movimiento en la base de datos
	        movimientoCajaRepository.save(movimiento);

	        // Asociamos el movimiento a la caja correspondiente
	        Caja caja = movimiento.getCaja();
	        caja.getMovimientos().add(movimiento); // Suponiendo que Caja tiene una lista de movimientos
	        cajaRepository.save(caja);
	}

	private MovimientoCaja toMovimientoCaja(Operacion operacion, TipoMovimiento tipo, 
			Caja caja, BigDecimal monto, BigDecimal montoEjecutado) {	
		return MovimientoCaja.builder()
						.fecha(operacion.getFechaCreacion())
						.tipo(tipo)
						.caja(caja)
						.operacion(operacion)
						.monto(monto)
						.montoEjecutado(montoEjecutado)	
						.build();	
	}

}
