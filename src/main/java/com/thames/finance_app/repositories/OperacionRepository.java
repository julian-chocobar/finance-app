package com.thames.finance_app.repositories;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thames.finance_app.models.CuentaCorriente;
import com.thames.finance_app.models.Moneda;
import com.thames.finance_app.models.Operacion;

@Repository
public interface OperacionRepository extends JpaRepository<Operacion, Long>{

	boolean existsByCuentaCorrienteId(Long cuentaCorrienteId);

	Page<Operacion> findAll(Specification<Operacion> spec, Pageable pageable);

	boolean existsByMonedaOrigen(Moneda moneda);

	boolean existsByMonedaConversion(Moneda moneda);

	boolean existsByCuentaCorriente(CuentaCorriente cuenta);

}
