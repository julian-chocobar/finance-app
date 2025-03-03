package com.thames.finance_app;


import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.thames.finance_app.dtos.OperacionRequest;
import com.thames.finance_app.dtos.OperacionResponse;
import com.thames.finance_app.enums.EstadoOperacion;
import com.thames.finance_app.enums.Moneda;
import com.thames.finance_app.enums.TipoEntrega;
import com.thames.finance_app.enums.TipoOperacion;
import com.thames.finance_app.exceptions.BusinessException;
import com.thames.finance_app.mappers.CtaCteMapper;
import com.thames.finance_app.mappers.OperacionMapper;
import com.thames.finance_app.models.Cliente;
import com.thames.finance_app.models.CuentaCorriente;
import com.thames.finance_app.models.Operacion;
import com.thames.finance_app.repositories.CtaCteRepository;
import com.thames.finance_app.repositories.OperacionRepository;
import com.thames.finance_app.services.CtaCteService;
import com.thames.finance_app.services.OperacionService;

@ExtendWith(MockitoExtension.class)
class OperacionServiceTest {

    @InjectMocks
    private OperacionService operacionService;

    @Mock
    private OperacionRepository operacionRepository;

    @Mock
    private CtaCteRepository ctaCteRepository;
    
    @Mock
    private CtaCteService ctaCteService;

    @Mock
    private OperacionMapper operacionMapper;

    @Mock
    private CtaCteMapper ctaCteMapper;

    @Test
    void givenValidCompra_whenCrearOperacion_thenSaldoClienteDisminuye() {
        // Datos de prueba


        // Simular comportamiento de repositorios y mapeo
;

        // Ejecutar método;

        // Verificar saldo actualizado

        // Verificar que la operación fue guardada
    }

    @Test
    void givenValidVenta_whenCrearOperacion_thenSaldoReferidoAumenta() {

    }


	@Test
    void givenInsufficientSaldo_whenCrearOperacion_thenThrowsException() {

    }
}
