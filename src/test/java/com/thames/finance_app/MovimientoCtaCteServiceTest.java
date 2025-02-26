package com.thames.finance_app;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.thames.finance_app.enums.Moneda;
import com.thames.finance_app.enums.TipoMovimiento;
import com.thames.finance_app.exceptions.BusinessException;
import com.thames.finance_app.models.Caja;
import com.thames.finance_app.models.CuentaCorriente;
import com.thames.finance_app.models.MovimientoCtaCte;
import com.thames.finance_app.repositories.CajaRepository;
import com.thames.finance_app.repositories.CtaCteRepository;
import com.thames.finance_app.repositories.MovimientoCtaCteRepository;
import com.thames.finance_app.services.MovimientoCtaCteService;

@ExtendWith(MockitoExtension.class)
class MovimientoCtaCteServiceTest {

    @InjectMocks
    private MovimientoCtaCteService movimientoCtaCteService;

    @Mock
    private MovimientoCtaCteRepository movimientoCtaCteRepository;

    @Mock
    private CtaCteRepository cuentaCorrienteRepository;

    @Mock
    private CajaRepository cajaRepository;

    private CuentaCorriente cuenta;
    private Caja caja;

    @BeforeEach
    void setUp() {
        cuenta = new CuentaCorriente();
        cuenta.setId(1L);
        cuenta.setSaldoPeso(BigDecimal.valueOf(1000)); // Saldo inicial

        caja = new Caja();
        caja.setId(1L);
        caja.setSaldoReal(BigDecimal.valueOf(5000));
        caja.setSaldoDisponible(BigDecimal.valueOf(5000));
        caja.setMoneda(Moneda.PESO);
    }

    @Test
    void registrarIngreso_DeberiaAumentarSaldoCuentaYCaja() {
        // Given
        BigDecimal monto = BigDecimal.valueOf(500);
        when(cuentaCorrienteRepository.findById(1L)).thenReturn(Optional.of(cuenta));
        when(cajaRepository.findByMoneda(Moneda.PESO)).thenReturn(Optional.of(caja));

        // When
        MovimientoCtaCte movimiento = movimientoCtaCteService.registrarMovimiento(1L, TipoMovimiento.INGRESO, Moneda.PESO, monto, "Depósito");

        // Then
        assertEquals(BigDecimal.valueOf(1500), cuenta.getSaldoPeso()); // 1000 + 500
        assertEquals(BigDecimal.valueOf(5500), caja.getSaldoReal());  // 5000 + 500
        assertEquals(BigDecimal.valueOf(5500), caja.getSaldoDisponible());  // 5000 + 500

        verify(cuentaCorrienteRepository).save(cuenta);
        verify(cajaRepository).save(caja);
        verify(movimientoCtaCteRepository).save(movimiento);
    }

    @Test
    void registrarEgreso_DeberiaDisminuirSaldoCuentaYCaja() {
        // Given
        BigDecimal monto = BigDecimal.valueOf(200);
        when(cuentaCorrienteRepository.findById(1L)).thenReturn(Optional.of(cuenta));
        when(cajaRepository.findByMoneda(Moneda.PESO)).thenReturn(Optional.of(caja));

        // When
        MovimientoCtaCte movimiento = movimientoCtaCteService.registrarMovimiento(1L, TipoMovimiento.EGRESO, Moneda.PESO, monto, "Pago");

        // Then
        assertEquals(BigDecimal.valueOf(800), cuenta.getSaldoPeso()); // 1000 - 200
        assertEquals(BigDecimal.valueOf(4800), caja.getSaldoReal());  // 5000 - 200
        assertEquals(BigDecimal.valueOf(4800), caja.getSaldoDisponible());  // 5000 - 200

        verify(cuentaCorrienteRepository).save(cuenta);
        verify(cajaRepository).save(caja);
        verify(movimientoCtaCteRepository).save(movimiento);
    }

    @Test
    void registrarEgreso_ConSaldoInsuficiente_DeberiaLanzarExcepcion() {
        // Given
        BigDecimal monto = BigDecimal.valueOf(1500); // Mayor al saldo de 1000
        when(cuentaCorrienteRepository.findById(1L)).thenReturn(Optional.of(cuenta));
        when(cajaRepository.findByMoneda(Moneda.PESO)).thenReturn(Optional.of(caja));

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () ->
                movimientoCtaCteService.registrarMovimiento(1L, TipoMovimiento.EGRESO, Moneda.PESO, monto, "Retiro")
        );

        assertEquals("Saldo insuficiente en PESO", exception.getMessage());

        // Verificamos que no se guardó ningún cambio en la base de datos
        verify(cuentaCorrienteRepository, never()).save(any());
        verify(cajaRepository, never()).save(any());
        verify(movimientoCtaCteRepository, never()).save(any());
    }
}

