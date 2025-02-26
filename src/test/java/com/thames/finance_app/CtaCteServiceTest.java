package com.thames.finance_app;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.thames.finance_app.dtos.CtaCteRequest;
import com.thames.finance_app.mappers.CtaCteMapper;
import com.thames.finance_app.models.Cliente;
import com.thames.finance_app.models.CuentaCorriente;
import com.thames.finance_app.repositories.ClienteRepository;
import com.thames.finance_app.repositories.CtaCteRepository;
import com.thames.finance_app.services.CtaCteService;

import java.math.BigDecimal;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class CtaCteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private CtaCteRepository cuentaCorrienteRepository;

    @Mock
    private CtaCteMapper ctaCteMapper;

    @InjectMocks
    private CtaCteService ctaCteService;

    private Cliente cliente;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setId(1L);
    }

    @Test
    void cuandoClienteNoExiste_debeLanzarExcepcion() {
        CtaCteRequest request = new CtaCteRequest();
        request.setClienteId(99L); // Cliente inexistente

        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            ctaCteService.crearCuentaCorriente(request);
        });

        assertEquals("El cliente no existe", exception.getMessage());
    }

    @Test
    void cuandoClienteExiste_saldosDebenSerCero() {
        CtaCteRequest request = new CtaCteRequest();
        request.setClienteId(1L);
        request.setSaldoDolar(BigDecimal.TEN); // Intento de saldo incorrecto

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

        CuentaCorriente cuentaCorriente = new CuentaCorriente();
        cuentaCorriente.setCliente(cliente);

        when(cuentaCorrienteRepository.save(any(CuentaCorriente.class)))
                .thenAnswer(invocation -> {
                    CuentaCorriente saved = invocation.getArgument(0);
                    assertEquals(BigDecimal.ZERO, saved.getSaldoDolar(), "El saldo en dólares debe ser 0");
                    assertEquals(BigDecimal.ZERO, saved.getSaldoPeso(), "El saldo en pesos debe ser 0");
                    return saved;
                });

        ctaCteService.crearCuentaCorriente(request);

        verify(cuentaCorrienteRepository).save(any(CuentaCorriente.class));
    }
    
    @Test
    void cuandoClienteYaTieneCuentaCorriente_debeLanzarExcepcion() {
        CtaCteRequest request = new CtaCteRequest();
        request.setClienteId(1L);

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(cuentaCorrienteRepository.existsByClienteId(1L)).thenReturn(true); // Ya existe una cuenta

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            ctaCteService.crearCuentaCorriente(request);
        });

        assertEquals("El cliente ya tiene una cuenta corriente", exception.getMessage());
    }

}
