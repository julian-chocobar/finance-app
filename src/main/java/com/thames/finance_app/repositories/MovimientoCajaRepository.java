package com.thames.finance_app.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.thames.finance_app.models.MovimientoCaja;
import com.thames.finance_app.models.Operacion;
import com.thames.finance_app.models.Caja;

@Repository
public interface MovimientoCajaRepository extends JpaRepository<MovimientoCaja, Long>, JpaSpecificationExecutor<MovimientoCaja>{

	boolean existsByCajaId(Long id);

	List<MovimientoCaja> findByOperacion(Operacion operacion);

	boolean existsByCaja(Caja caja);

	@Query("SELECT m FROM MovimientoCaja m ORDER BY m.fecha DESC")
	Page<MovimientoCaja> findAllOrderFechaDesc(Pageable pageable);

}
