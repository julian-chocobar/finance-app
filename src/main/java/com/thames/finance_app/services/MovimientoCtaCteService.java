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
        BigDecimal saldoActual = obtenerSaldoPorMoneda(cuenta, moneda);
        
        if (tipo == TipoMovimiento.INGRESO) {
            actualizarSaldoPorMoneda(cuenta, moneda, saldoActual.add(monto));
            caja.setSaldoReal(caja.getSaldoReal().add(monto));
            caja.setSaldoDisponible(caja.getSaldoDisponible().add(monto));
        } else if (tipo == TipoMovimiento.EGRESO) {
            actualizarSaldoPorMoneda(cuenta, moneda, saldoActual.subtract(monto));
            caja.setSaldoReal(caja.getSaldoReal().subtract(monto));
            caja.setSaldoDisponible(caja.getSaldoDisponible().subtract(monto));
        }
    }
    
    private BigDecimal obtenerSaldoPorMoneda(CuentaCorriente cuenta, Moneda moneda) {
        return switch (moneda) {
            case PESO -> cuenta.getSaldoPeso();
            case USD -> cuenta.getSaldoDolar();
            case EURO -> cuenta.getSaldoEuro();
            case REAL -> cuenta.getSaldoReal();
            case CRYPTO -> cuenta.getSaldoCrypto();
            default -> throw new BusinessException("Moneda no soportada: " + moneda);
        };
    }

    private void actualizarSaldoPorMoneda(CuentaCorriente cuenta, Moneda moneda, BigDecimal nuevoSaldo) {
        switch (moneda) {
            case PESO -> cuenta.setSaldoPeso(nuevoSaldo);
            case USD -> cuenta.setSaldoDolar(nuevoSaldo);
            case EURO -> cuenta.setSaldoEuro(nuevoSaldo);
            case REAL -> cuenta.setSaldoReal(nuevoSaldo);
            case CRYPTO -> cuenta.setSaldoCrypto(nuevoSaldo);
            default -> throw new BusinessException("Moneda no soportada: " + moneda);
        }
    }
	
}

