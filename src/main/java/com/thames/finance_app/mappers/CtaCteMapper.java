package com.thames.finance_app.mappers;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.thames.finance_app.dtos.CtaCteRequest;
import com.thames.finance_app.dtos.CtaCteResponse;
import com.thames.finance_app.dtos.MovimientoCtaCteResponse;
import com.thames.finance_app.enums.Moneda;
import com.thames.finance_app.models.Cliente;
import com.thames.finance_app.models.CuentaCorriente;
import com.thames.finance_app.models.MovimientoCtaCte;

@Component
public class CtaCteMapper {

    public CtaCteResponse toResponse(CuentaCorriente cuenta) {
        return CtaCteResponse.builder()
                .id(cuenta.getId())
                .clienteId(cuenta.getCliente().getId())
                .saldoPorMoneda(new HashMap<>(cuenta.getSaldos())) // Copia el mapa para evitar modificaciones accidentales
                .build();
    }

    public CtaCteResponse toResponse2(CuentaCorriente cuentaCorriente, Page<MovimientoCtaCte> movimientos) {
        return CtaCteResponse.builder()
                .id(cuentaCorriente.getId())
                .clienteId(cuentaCorriente.getCliente().getId())
                .saldoPorMoneda(new HashMap<>(cuentaCorriente.getSaldos()))
                .movimientos(movimientos.getContent().stream()
                        .map(this::toMovimientoResponse)
                        .toList())
                .totalMovimientos(movimientos.getTotalElements())
                .totalPaginas(movimientos.getTotalPages())
                .build();
    }

    public MovimientoCtaCteResponse toMovimientoResponse(MovimientoCtaCte movimiento) {
        return MovimientoCtaCteResponse.builder()
                .id(movimiento.getId())
                .tipo(movimiento.getTipoMovimiento())
                .moneda(movimiento.getMoneda())
                .monto(movimiento.getMonto())
                .fecha(movimiento.getFecha())
                .build();
    }

    public CuentaCorriente toEntity(CtaCteRequest request, Cliente cliente) {
        Map<Moneda, BigDecimal> saldosIniciales = new HashMap<>();
        for (Moneda moneda : Moneda.values()) {
            saldosIniciales.put(moneda, BigDecimal.ZERO);
        }

        return CuentaCorriente.builder()
                .cliente(cliente)
                .saldos(saldosIniciales)
                .build();
    }
}
