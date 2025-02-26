package com.thames.finance_app.mappers;

import java.math.BigDecimal;
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
                .saldoPeso(cuenta.getSaldoPeso())
                .saldoDolar(cuenta.getSaldoDolar())
                .saldoEuro(cuenta.getSaldoEuro())
                .saldoReal(cuenta.getSaldoReal())
                .saldoCrypto(cuenta.getSaldoCrypto())
                .build();
    }
    
    public CtaCteResponse toResponse2(CuentaCorriente cuentaCorriente, Page<MovimientoCtaCte> movimientos) {
        return CtaCteResponse.builder()
                .id(cuentaCorriente.getId())
                .clienteId(cuentaCorriente.getCliente().getId())
                .saldoPorMoneda(Map.of(
                        Moneda.PESO, cuentaCorriente.getSaldoPeso(),
                        Moneda.USD, cuentaCorriente.getSaldoDolar(),
                        Moneda.EURO, cuentaCorriente.getSaldoEuro(),
                        Moneda.REAL, cuentaCorriente.getSaldoReal(),
                        Moneda.CRYPTO, cuentaCorriente.getSaldoCrypto()
                ))
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
                .tipo(movimiento.getTipo())
                .moneda(movimiento.getMoneda())
                .monto(movimiento.getMonto())
                .descripcion(movimiento.getDescripcion())
                .fecha(movimiento.getFecha())
                .build();
    }

    public CuentaCorriente toEntity(CtaCteRequest request, Cliente cliente) {
        return CuentaCorriente.builder()
                .cliente(cliente)
                .saldoPeso(BigDecimal.ZERO)
                .saldoDolar(BigDecimal.ZERO)
                .saldoEuro(BigDecimal.ZERO)
                .saldoReal(BigDecimal.ZERO)
                .saldoCrypto(BigDecimal.ZERO)
                .build();
    }
}
