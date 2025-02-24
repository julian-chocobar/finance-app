package com.thames.finance_app.mappers;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.thames.finance_app.dtos.CtaCteRequest;
import com.thames.finance_app.dtos.CtaCteResponse;
import com.thames.finance_app.models.Cliente;
import com.thames.finance_app.models.CuentaCorriente;

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
