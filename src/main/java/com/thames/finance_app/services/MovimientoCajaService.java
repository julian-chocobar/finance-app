package com.thames.finance_app.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

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

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MovimientoCajaService {

	private final MovimientoCajaRepository movimientoCajaRepository;
	private final CajaRepository cajaRepository;
	private final CajaMapper cajaMapper;

	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

	
	
	public MovimientoCajaDTO obtenerMovimientoPorId(Long id) {
		return cajaMapper.toMovimientoDTO(movimientoCajaRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Movimiento no encontrado")));
	}

    public MovimientoCaja obtenerEntidadPorId(Long id) {
        return movimientoCajaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Movimiento no encontrado"));
    }
	
	public Page<MovimientoCajaDTO> obtenerMovimientosFiltrados(Specification<MovimientoCaja> spec, Pageable pageable) {
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

    public void actualizarMovimiento(Long id, MovimientoCajaDTO dto) {
        MovimientoCaja movimiento = movimientoCajaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Movimiento no encontrado"));
        cajaMapper.updateEntity(movimiento, dto);
        movimientoCajaRepository.save(movimiento);
    }

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

	public void eliminarMovimiento(Long id) {
	    // Verificar si el movimiento existe
	    if (!movimientoCajaRepository.existsById(id)) {
	        throw new EntityNotFoundException("No se encontró el movimiento con ID: " + id);
	    }
	    
	    // Obtener el movimiento para actualizar los saldos de la caja
	    MovimientoCaja movimiento = movimientoCajaRepository.findById(id)
	            .orElseThrow(() -> new EntityNotFoundException("No se encontró el movimiento con ID: " + id));
	    
	    // Actualizar los saldos de la caja (revertir el efecto del movimiento)
	    Caja caja = movimiento.getCaja();	          
	    BigDecimal montoEjecutado = movimiento.getMontoEjecutado();
	    
	    if (movimiento.getTipo() == TipoMovimiento.INGRESO) {
	        caja.setSaldoReal(caja.getSaldoReal().subtract(montoEjecutado));
	        caja.setSaldoDisponible(caja.getSaldoDisponible().subtract(montoEjecutado));
	    } else if (movimiento.getTipo() == TipoMovimiento.EGRESO) {
	        caja.setSaldoReal(caja.getSaldoReal().add(montoEjecutado));
	        caja.setSaldoDisponible(caja.getSaldoDisponible().add(montoEjecutado));
	    }	    
	    // Guardar los cambios en la caja
	    cajaRepository.save(caja);    
	    // Eliminar el movimiento
	    movimientoCajaRepository.deleteById(id);
	}

}
