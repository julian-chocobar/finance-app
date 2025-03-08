package com.thames.finance_app.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thames.finance_app.enums.Moneda;
import com.thames.finance_app.models.Caja;

@Repository
public interface CajaRepository extends JpaRepository<Caja, Long>{

	Optional<Caja> findByMoneda(Moneda moneda);
	

}
