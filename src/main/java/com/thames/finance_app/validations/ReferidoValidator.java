package com.thames.finance_app.validations;

import com.thames.finance_app.dtos.OperacionRequest;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ReferidoValidator implements ConstraintValidator<ValidReferido, OperacionRequest> {

    @Override
    public void initialize(ValidReferido constraintAnnotation) {
        // Inicialización si es necesaria
    }

    @Override
    public boolean isValid(OperacionRequest operacionRequest, ConstraintValidatorContext context) {
        if (operacionRequest.getNombreReferido() != null) {
            // Si nombreReferido no es null, los demás campos no pueden ser null
            return operacionRequest.getPuntosReferido() != null &&
                   operacionRequest.getMonedaReferido() != null &&
                   operacionRequest.getGananciaReferido() != null;
        } else {
            // Si nombreReferido es null, los demás campos deben ser null
            return operacionRequest.getPuntosReferido() == null &&
                   operacionRequest.getMonedaReferido() == null &&
                   operacionRequest.getGananciaReferido() == null;
        }
    }
}