package com.thames.finance_app.specifications;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.thames.finance_app.models.MovimientoCaja;

import jakarta.persistence.criteria.Predicate;

public class MovimientoCajaSpecification {
	
    @SuppressWarnings("unused")
	public static Specification<MovimientoCaja> filtrarMovimientos(
            String nombreCaja, String tipo, LocalDateTime fechaDesde, LocalDateTime fechaHasta,
            BigDecimal montoMinimo, BigDecimal montoMaximo, Long idOperacion) {
        
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (nombreCaja != null && !nombreCaja.isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("caja").get("nombre")),
                        "%" + nombreCaja.toLowerCase() + "%"
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

            if (montoMinimo != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("monto"), montoMinimo));
            }
            
            if (montoMaximo != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("monto"), montoMaximo));
            }
            if (idOperacion != null ) {
                predicates.add(criteriaBuilder.equal(root.get("idOperacion"), idOperacion));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
           
        };
    }

}
