package com.thames.finance_app.specifications;

import org.springframework.data.jpa.domain.Specification;

import com.thames.finance_app.enums.TipoOperacion;
import com.thames.finance_app.models.Moneda;
import com.thames.finance_app.models.Operacion;

import jakarta.persistence.criteria.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;


public class OperacionSpecification {

    @SuppressWarnings("unused")
	public static Specification<Operacion> filtrarPorParametros(
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            BigDecimal montoOrigen,
            Moneda monedaOrigen,
            BigDecimal montoConversion,
            Moneda monedaConversion,
            TipoOperacion tipo,
            String clienteNombre) {

        return (Root<Operacion> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            Predicate predicate = cb.conjunction();

            if (fechaInicio != null && fechaFin != null) {
                predicate = cb.and(predicate, cb.between(root.get("fecha"), fechaInicio, fechaFin));
            }
            if (montoOrigen != null) {
                predicate = cb.and(predicate, cb.equal(root.get("montoOrigen"), montoOrigen));
            }
            if (monedaOrigen != null) {
                predicate = cb.and(predicate, cb.equal(root.get("monedaOrigen"), monedaOrigen));
            }     
            if (montoConversion != null) {
                predicate = cb.and(predicate, cb.equal(root.get("montoConversion"), montoConversion));
            }
            if (monedaConversion != null) {
                predicate = cb.and(predicate, cb.equal(root.get("monedaConversion"), monedaConversion));
            }
            if (tipo != null) {
                predicate = cb.and(predicate, cb.equal(root.get("tipo"), tipo));
            }
            if (clienteNombre != null) {
                predicate = cb.and(predicate, cb.equal(root.get("cuentaCorriente").get("titular").get("nombre"), clienteNombre));
            }

            return predicate;
        };
    }
}
