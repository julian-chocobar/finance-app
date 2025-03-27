package com.thames.finance_app.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thames.finance_app.models.Caja;
import com.thames.finance_app.models.Moneda;

@Repository
public interface CajaRepository extends JpaRepository<Caja, Long>{

	Optional<Caja> findByMoneda(Moneda moneda);

	Optional<Caja> findByNombre(String nombre);
	
	Page<Caja> findAll(Specification<Caja> spec, Pageable pageable);
	

}
