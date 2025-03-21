package com.thames.finance_app.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thames.finance_app.models.MovimientoCaja;
import com.thames.finance_app.models.Operacion;

@Repository
public interface MovimientoCajaRepository extends JpaRepository<MovimientoCaja, Long> {

	boolean existsByCajaId(Long id);

	List<MovimientoCaja> findByOperacion(Operacion operacion);

}
