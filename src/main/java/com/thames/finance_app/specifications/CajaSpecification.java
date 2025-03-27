package com.thames.finance_app.specifications;

import java.math.BigDecimal;

import org.springframework.data.jpa.domain.Specification;

import com.thames.finance_app.models.Caja;

import jakarta.persistence.criteria.*;

public class CajaSpecification {
	
	public static Specification<Caja> filtrarPorParametros(
			String nombre, String moneda, BigDecimal saldoMinimo){
		return (Root<Caja> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            Predicate predicate = cb.conjunction();
            if (nombre != null && nombre != null) {
                predicate = cb.and(predicate, cb.equal(root.get("nombre"), nombre));
            }
            if (moneda != null && moneda != null) {
                predicate = cb.and(predicate, cb.equal(root.get("nombre"), moneda));
            }
            if (saldoMinimo != null && saldoMinimo != null) {
                predicate = cb.and(predicate, cb.equal(root.get("nombre"), saldoMinimo));
            }
            
            return predicate;
            
		};
            
	}
			

}
