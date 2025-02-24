package com.thames.finance_app.services;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.thames.finance_app.dtos.CtaCteRequest;
import com.thames.finance_app.dtos.CtaCteResponse;
import com.thames.finance_app.enums.Moneda;
import com.thames.finance_app.enums.TipoMovimiento;
import com.thames.finance_app.exceptions.BusinessException;
import com.thames.finance_app.mappers.CtaCteMapper;
import com.thames.finance_app.models.Cliente;
import com.thames.finance_app.models.CuentaCorriente;
import com.thames.finance_app.repositories.ClienteRepository;
import com.thames.finance_app.repositories.CtaCteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CtaCteService {

    private final CtaCteRepository ctaCteRepository;
    private final ClienteRepository clienteRepository;
    private final CtaCteMapper ctaCteMapper;

    public CtaCteResponse obtenerCuentaPorId(Long cuentaId) {
        CuentaCorriente cuenta = ctaCteRepository.findById(cuentaId)
                .orElseThrow(() -> new BusinessException("Cuenta Corriente no encontrada"));
        return ctaCteMapper.toResponse(cuenta);
    }

    public CtaCteResponse obtenerCuentaPorClienteId(Long clienteId) {
        CuentaCorriente cuenta = ctaCteRepository.findByClienteId(clienteId)
                .orElseThrow(() -> new BusinessException("Cuenta Corriente no encontrada para el cliente"));
        return ctaCteMapper.toResponse(cuenta);
    }

    public CtaCteResponse crearCuenta(CtaCteRequest request) {
        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new BusinessException("Cliente no encontrado"));

        if (ctaCteRepository.existsByCliente(cliente)) {
            throw new BusinessException("El cliente ya tiene una cuenta corriente");
        }

        CuentaCorriente cuenta = ctaCteMapper.toEntity(request, cliente);
        ctaCteRepository.save(cuenta);
        return ctaCteMapper.toResponse(cuenta);
    }

    public void eliminarCuenta(Long cuentaId) {
        CuentaCorriente cuenta = ctaCteRepository.findById(cuentaId)
                .orElseThrow(() -> new BusinessException("Cuenta Corriente no encontrada"));
        ctaCteRepository.delete(cuenta);
    }

    public CtaCteResponse actualizarSaldos(Long cuentaId, BigDecimal monto, Moneda moneda, TipoMovimiento tipo) {
        CuentaCorriente cuenta = ctaCteRepository.findById(cuentaId)
                .orElseThrow(() -> new BusinessException("Cuenta Corriente no encontrada"));

        switch (moneda) {
            case PESO:
                cuenta.setSaldoPeso(calcularNuevoSaldo(cuenta.getSaldoPeso(), monto, tipo));
                break;
            case USD:
                cuenta.setSaldoDolar(calcularNuevoSaldo(cuenta.getSaldoDolar(), monto, tipo));
                break;
            case EURO:
                cuenta.setSaldoEuro(calcularNuevoSaldo(cuenta.getSaldoEuro(), monto, tipo));
                break;
            case REAL:
                cuenta.setSaldoReal(calcularNuevoSaldo(cuenta.getSaldoReal(), monto, tipo));
                break;
            case CRYPTO:
                cuenta.setSaldoCrypto(calcularNuevoSaldo(cuenta.getSaldoCrypto(), monto, tipo));
                break;
            default:
                throw new BusinessException("Moneda no soportada");
        }

        ctaCteRepository.save(cuenta);
        return ctaCteMapper.toResponse(cuenta);
    }

    private BigDecimal calcularNuevoSaldo(BigDecimal saldoActual, BigDecimal monto, TipoMovimiento tipo) {
        return tipo == TipoMovimiento.INGRESO ? saldoActual.add(monto) : saldoActual.subtract(monto);
    }
}

