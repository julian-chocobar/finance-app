package com.thames.finance_app.validations;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ ElementType.TYPE }) // Aplica a nivel de clase
@Retention(RetentionPolicy.RUNTIME) // Disponible en tiempo de ejecución
@Constraint(validatedBy = ReferidoValidator.class) // Clase que implementa la validación
public @interface ValidReferido {
    String message() default "Los campos relacionados con el referido no son válidos.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
