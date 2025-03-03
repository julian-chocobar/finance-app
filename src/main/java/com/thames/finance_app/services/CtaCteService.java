package com.thames.finance_app.services;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.thames.finance_app.dtos.CtaCteRequest;
import com.thames.finance_app.dtos.CtaCteResponse;
import com.thames.finance_app.dtos.MovimientoCtaCteResponse;
import com.thames.finance_app.enums.Moneda;
import com.thames.finance_app.enums.TipoMovimiento;
import com.thames.finance_app.exceptions.BusinessException;
import com.thames.finance_app.mappers.CtaCteMapper;
import com.thames.finance_app.models.Cliente;
import com.thames.finance_app.models.CuentaCorriente;
import com.thames.finance_app.models.MovimientoCtaCte;
import com.thames.finance_app.repositories.ClienteRepository;
import com.thames.finance_app.repositories.CtaCteRepository;
import com.thames.finance_app.repositories.MovimientoCtaCteRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CtaCteService {

    private final CtaCteRepository ctaCteRepository;
    private final ClienteRepository clienteRepository;
    private MovimientoCtaCteRepository movimientoCtaCteRepository;
    private final CtaCteMapper ctaCteMapper;

    public CtaCteResponse obtenerResponsePorId(Long cuentaId) {
        CuentaCorriente cuenta = ctaCteRepository.findById(cuentaId)
                .orElseThrow(() -> new BusinessException("Cuenta Corriente no encontrada"));
        return ctaCteMapper.toResponse(cuenta);
    }
    
    public CuentaCorriente obtenerCuentaPorId(Long cuentaId) {
        CuentaCorriente cuenta = ctaCteRepository.findById(cuentaId)
                .orElseThrow(() -> new BusinessException("Cuenta Corriente no encontrada"));
        return cuenta;
    }

    public CtaCteResponse obtenerCuentaPorClienteId(Long clienteId) {
        CuentaCorriente cuenta = ctaCteRepository.findByClienteId(clienteId)
                .orElseThrow(() -> new BusinessException("Cuenta Corriente no encontrada para el cliente"));
        return ctaCteMapper.toResponse(cuenta);
    }
 
    @Transactional
    public CtaCteResponse crearCuentaCorriente(CtaCteRequest request) {
        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new IllegalArgumentException("El cliente no existe"));

        if (ctaCteRepository.existsByClienteId(cliente.getId())) {
            throw new BusinessException("El cliente ya tiene una cuenta corriente");
        }

        CuentaCorriente cuentaCorriente = new CuentaCorriente();
        cuentaCorriente.setCliente(cliente);
        ctaCteRepository.save(cuentaCorriente);

        return ctaCteMapper.toResponse2(cuentaCorriente, Page.empty());
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
                cuenta.setSaldoPesos(calcularNuevoSaldo(cuenta.getSaldoPesos(), monto, tipo));
                break;
            case USD:
                cuenta.setSaldoDolares(calcularNuevoSaldo(cuenta.getSaldoDolares(), monto, tipo));
                break;
            case EURO:
                cuenta.setSaldoEuros(calcularNuevoSaldo(cuenta.getSaldoEuros(), monto, tipo));
                break;
            case REAL:
                cuenta.setSaldoReales(calcularNuevoSaldo(cuenta.getSaldoReales(), monto, tipo));
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
    
    @Transactional
    public void actualizarSaldo(CuentaCorriente cuentaCorriente, Moneda moneda, BigDecimal monto, boolean esIngreso) {
        if (cuentaCorriente == null) {
            throw new IllegalArgumentException("La cuenta corriente no puede ser nula.");
        }

        // Obtener el saldo actual de la moneda específica
        BigDecimal saldoActual = cuentaCorriente.getSaldoPorMoneda(moneda);
        BigDecimal nuevoSaldo = esIngreso ? saldoActual.add(monto) : saldoActual.subtract(monto);

        if (nuevoSaldo.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("No hay saldo suficiente para realizar la operación.");
        }

        // Actualizar el saldo en la cuenta
        cuentaCorriente.setSaldoPorMoneda(moneda, nuevoSaldo);
        ctaCteRepository.save(cuentaCorriente);
    }

    public Page<MovimientoCtaCteResponse> obtenerMovimientos(Long cuentaId, LocalDate fechaDesde, LocalDate fechaHasta,
            LocalDate fechaExacta, BigDecimal monto,
            Moneda moneda, TipoMovimiento tipo,
            Pageable pageable) {
		Page<MovimientoCtaCte> movimientos = filtrarMovimientos(cuentaId, fechaExacta, fechaDesde, fechaHasta, monto, moneda, tipo, pageable);
		return movimientos.map(ctaCteMapper::toMovimientoResponse);
	}
    
    public BigDecimal obtenerSaldoPorMoneda(CuentaCorriente cuenta, Moneda moneda) {
        switch (moneda) {
            case USD:
                return cuenta.getSaldoDolares();
            case PESO:
                return cuenta.getSaldoPesos();
            case REAL:
                return cuenta.getSaldoReales();
            case EURO:
                return cuenta.getSaldoEuros();
            default:
                throw new BusinessException("Moneda no soportada: " + moneda);
        }
    }

    public void setSaldoPorMoneda(CuentaCorriente cuenta, BigDecimal nuevoSaldo, Moneda moneda) {
        switch (moneda) {
            case USD:
                cuenta.setSaldoDolares(nuevoSaldo);
                break;
            case PESO:
                cuenta.setSaldoPesos(nuevoSaldo);
                break;
            case REAL:
                cuenta.setSaldoReales(nuevoSaldo);
                break;
            case EURO:
                cuenta.setSaldoEuros(nuevoSaldo);
                break;
            default:
                throw new BusinessException("Moneda no soportada: " + moneda);
        }
    }
    
    public Page<MovimientoCtaCte> filtrarMovimientos(
            Long cuentaCorrienteId,
            LocalDate fechaExacta,
            LocalDate fechaDesde,
            LocalDate fechaHasta,
            BigDecimal monto,
            Moneda moneda,
            TipoMovimiento tipoMovimiento,
            Pageable pageable) {
            return movimientoCtaCteRepository.filtrarMovimientos(
                cuentaCorrienteId, fechaExacta, fechaDesde, fechaHasta, monto, moneda, tipoMovimiento, pageable);
    }



}

