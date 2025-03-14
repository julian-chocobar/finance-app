package com.thames.finance_app.specifications;


import org.springframework.data.jpa.domain.Specification;
import com.thames.finance_app.models.MovimientoCtaCte;

import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MovimientoCtaCteSpecification {

    @SuppressWarnings("unused")
	public static Specification<MovimientoCtaCte> filtrarMovimientos(
            String nombreTitular, String tipo, Date fechaDesde, Date fechaHasta,
            BigDecimal monto, String moneda) {
        
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (nombreTitular != null && !nombreTitular.isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("cuentaCorriente").get("titular").get("nombre")),
                        "%" + nombreTitular.toLowerCase() + "%"
                ));
            }

            if (tipo != null && !tipo.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("tipo"), tipo));
            }

            if (fechaDesde != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("fecha"), fechaDesde));
            }

            if (fechaHasta != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("fecha"), fechaHasta));
            }

            if (monto != null) {
                predicates.add(criteriaBuilder.equal(root.get("monto"), monto));
            }

            if (moneda != null && !moneda.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("moneda"), moneda));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
