package com.thames.finance_app;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.thames.finance_app.dtos.CtaCteRequest;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CtaCteRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void cuandoClienteIdEsValido_noDebeHaberViolaciones() {
        CtaCteRequest request = new CtaCteRequest();
        request.setClienteId(1L);

        Set<ConstraintViolation<CtaCteRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "No debería haber violaciones con clienteId válido");
    }

    @Test
    void cuandoClienteIdEsNulo_debeGenerarViolacion() {
        CtaCteRequest request = new CtaCteRequest();
        request.setClienteId(null);

        Set<ConstraintViolation<CtaCteRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty(), "Debe haber violación si clienteId es nulo");
    }

    @Test
    void cuandoClienteIdEsNegativo_debeGenerarViolacion() {
        CtaCteRequest request = new CtaCteRequest();
        request.setClienteId(-1L);

        Set<ConstraintViolation<CtaCteRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty(), "Debe haber violación si clienteId es negativo");
    }

    @Test
    void cuandoSaldoNoEsCero_debeGenerarViolacion() {
        CtaCteRequest request = new CtaCteRequest();
        request.setClienteId(1L);
        request.setSaldoDolar(BigDecimal.TEN);

        Set<ConstraintViolation<CtaCteRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty(), "Debe haber violación si el saldo inicial no es 0");
    }

}
