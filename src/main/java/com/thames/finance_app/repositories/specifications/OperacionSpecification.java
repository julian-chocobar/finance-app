package com.thames.finance_app.repositories.specifications;

import org.springframework.data.jpa.domain.Specification;

import com.thames.finance_app.enums.Moneda;
import com.thames.finance_app.enums.TipoOperacion;
import com.thames.finance_app.models.Operacion;

import jakarta.persistence.criteria.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;


public class OperacionSpecification {

    public static Specification<Operacion> filtrarPorParametros(
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            BigDecimal monto,
            Moneda moneda,
            TipoOperacion tipo,
            Long clienteId) {

        return (Root<Operacion> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            Predicate predicate = cb.conjunction();

            if (fechaInicio != null && fechaFin != null) {
                predicate = cb.and(predicate, cb.between(root.get("fecha"), fechaInicio, fechaFin));
            }
            if (monto != null) {
                predicate = cb.and(predicate, cb.equal(root.get("montoOrigen"), monto));
            }
            if (moneda != null) {
                predicate = cb.and(predicate, cb.equal(root.get("monedaOrigen"), moneda));
            }
            if (tipo != null) {
                predicate = cb.and(predicate, cb.equal(root.get("tipo"), tipo));
            }
            if (clienteId != null) {
                predicate = cb.and(predicate, cb.equal(root.get("cuentaCorriente").get("cliente").get("id"), clienteId));
            }

            return predicate;
        };
    }
}
