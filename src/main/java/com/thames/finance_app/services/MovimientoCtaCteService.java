package com.thames.finance_app.services;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.thames.finance_app.enums.Moneda;
import com.thames.finance_app.enums.TipoMovimiento;
import com.thames.finance_app.exceptions.BusinessException;
import com.thames.finance_app.models.CuentaCorriente;
import com.thames.finance_app.models.MovimientoCtaCte;
import com.thames.finance_app.models.Caja;
import com.thames.finance_app.repositories.CajaRepository;
import com.thames.finance_app.repositories.CtaCteRepository;
import com.thames.finance_app.repositories.MovimientoCtaCteRepository;

import jakarta.transaction.Transactional;

@Service
public class MovimientoCtaCteService {

    @Autowired
    private MovimientoCtaCteRepository movimientoCtaCteRepository;

    @Autowired
    private CtaCteRepository cuentaCorrienteRepository;

    @Autowired
    private CajaRepository cajaRepository;

    @Transactional
    public MovimientoCtaCte registrarMovimiento(Long cuentaId, TipoMovimiento tipo, Moneda moneda, BigDecimal monto, String descripcion) {
        CuentaCorriente cuenta = cuentaCorrienteRepository.findById(cuentaId)
                .orElseThrow(() -> new BusinessException("Cuenta Corriente no encontrada"));

        Caja caja = cajaRepository.findByMoneda(moneda)
                .orElseThrow(() -> new BusinessException("No existe una caja para la moneda " + moneda));

        validarSaldoParaEgreso(cuenta, tipo, moneda, monto);

        MovimientoCtaCte movimiento = MovimientoCtaCte.builder()
                .cuentaCorriente(cuenta)
                .tipo(tipo)
                .moneda(moneda)
                .monto(monto)
                .descripcion(descripcion)
                .build();

        actualizarSaldos(cuenta, caja, tipo, moneda, monto);

        cuentaCorrienteRepository.save(cuenta);
        cajaRepository.save(caja);
        movimientoCtaCteRepository.save(movimiento);

        return movimiento;
    }

    private void actualizarSaldos(CuentaCorriente cuenta, Caja caja, TipoMovimiento tipo, Moneda moneda, BigDecimal monto) {
        if (tipo == TipoMovimiento.INGRESO) {
            modificarSaldos(cuenta, caja, moneda, monto, true);
        } else if (tipo == TipoMovimiento.EGRESO) {
            modificarSaldos(cuenta, caja, moneda, monto, false);
        }
    }

    private void modificarSaldos(CuentaCorriente cuenta, Caja caja, Moneda moneda, BigDecimal monto, boolean esIngreso) {
        BigDecimal saldoCuenta = obtenerSaldoPorMoneda(cuenta, moneda);
        BigDecimal saldoCajaReal = caja.getSaldoReal();
        BigDecimal saldoCajaDisponible = caja.getSaldoDisponible();

        BigDecimal nuevoSaldoCuenta = esIngreso ? saldoCuenta.add(monto) : saldoCuenta.subtract(monto);
        BigDecimal nuevoSaldoCajaReal = esIngreso ? saldoCajaReal.add(monto) : saldoCajaReal.subtract(monto);
        BigDecimal nuevoSaldoCajaDisponible = esIngreso ? saldoCajaDisponible.add(monto) : saldoCajaDisponible.subtract(monto);

        actualizarSaldoPorMoneda(cuenta, moneda, nuevoSaldoCuenta);
        caja.setSaldoReal(nuevoSaldoCajaReal);
        caja.setSaldoDisponible(nuevoSaldoCajaDisponible);
    }

    private void validarSaldoParaEgreso(CuentaCorriente cuenta, TipoMovimiento tipo, Moneda moneda, BigDecimal monto) {
        if (tipo == TipoMovimiento.EGRESO) {
            BigDecimal saldoActual = obtenerSaldoPorMoneda(cuenta, moneda);
            if (saldoActual.compareTo(monto) < 0) {
                throw new BusinessException("Saldo insuficiente en " + moneda);
            }
        }
    }

    private BigDecimal obtenerSaldoPorMoneda(CuentaCorriente cuenta, Moneda moneda) {
        return switch (moneda) {
            case PESO -> cuenta.getSaldoPesos();
            case USD -> cuenta.getSaldoDolares();
            case EURO -> cuenta.getSaldoEuros();
            case REAL -> cuenta.getSaldoReales();
            case CRYPTO -> cuenta.getSaldoCrypto();
        };
    }

    private void actualizarSaldoPorMoneda(CuentaCorriente cuenta, Moneda moneda, BigDecimal nuevoSaldo) {
        switch (moneda) {
            case PESO -> cuenta.setSaldoPesos(nuevoSaldo);
            case USD -> cuenta.setSaldoDolares(nuevoSaldo);
            case EURO -> cuenta.setSaldoEuros(nuevoSaldo);
            case REAL -> cuenta.setSaldoReales(nuevoSaldo);
            case CRYPTO -> cuenta.setSaldoCrypto(nuevoSaldo);
        }
    }
	
}

