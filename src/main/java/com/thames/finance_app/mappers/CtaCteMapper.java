package com.thames.finance_app.mappers;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.thames.finance_app.dtos.CtaCteRequest;
import com.thames.finance_app.dtos.CtaCteResponse;
import com.thames.finance_app.dtos.MovimientoCtaCteDTO;
import com.thames.finance_app.models.Titular;
import com.thames.finance_app.services.MonedaService;

import lombok.RequiredArgsConstructor;

import com.thames.finance_app.models.CuentaCorriente;
import com.thames.finance_app.models.Moneda;
import com.thames.finance_app.models.MovimientoCtaCte;

@Component
@RequiredArgsConstructor
public class CtaCteMapper {
	
	private final MonedaService monedaService;
	

    public CtaCteResponse toResponse(CuentaCorriente cuenta) {
        return CtaCteResponse.builder()
                .id(cuenta.getId())
                .clienteId(cuenta.getTitular().getId())
                .saldoPorMoneda(new HashMap<>(cuenta.getSaldos())) // Copia el mapa para evitar modificaciones accidentales
                .build();
    }

    public CtaCteResponse toResponse2(CuentaCorriente cuentaCorriente, Page<MovimientoCtaCte> movimientos) {
        return CtaCteResponse.builder()
                .id(cuentaCorriente.getId())
                .clienteId(cuentaCorriente.getTitular().getId())
                .saldoPorMoneda(new HashMap<>(cuentaCorriente.getSaldos()))
                .movimientos(movimientos.getContent().stream()
                        .map(this::toMovimientoResponse)
                        .toList())
                .totalMovimientos(movimientos.getTotalElements())
                .totalPaginas(movimientos.getTotalPages())
                .build();
    }

    public MovimientoCtaCteDTO toMovimientoResponse(MovimientoCtaCte movimiento) {
        return MovimientoCtaCteDTO.builder()
                .id(movimiento.getId())
                .tipo(movimiento.getTipoMovimiento())
                .moneda(movimiento.getMoneda())
                .monto(movimiento.getMonto())
                .fecha(movimiento.getFecha())
                .build();
    }

    public CuentaCorriente toEntity(CtaCteRequest request, Titular cliente) {
    	// Obtener todas las monedas desde la base de datos
        List<Moneda> monedas = monedaService.listarTodas(); 

        // Inicializar los saldos con BigDecimal.ZERO para cada moneda
        Map<Moneda, BigDecimal> saldosIniciales = new HashMap<>();
        for (Moneda moneda : monedas) {
            saldosIniciales.put(moneda, BigDecimal.ZERO);
        }

        return CuentaCorriente.builder()
                .titular(cliente)
                .saldos(saldosIniciales)
                .build();
    }
}
