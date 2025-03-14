package com.thames.finance_app.repositories;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.thames.finance_app.models.MovimientoCtaCte;
import com.thames.finance_app.models.Operacion;


@Repository
public interface MovimientoCtaCteRepository  extends JpaRepository<MovimientoCtaCte, Long>,JpaSpecificationExecutor<MovimientoCtaCte> {

	List<MovimientoCtaCte> findByCuentaCorrienteId(Long cuentaId);
		
	@Query("SELECT m FROM MovimientoCtaCte m ORDER BY m.fecha DESC")
	Page<MovimientoCtaCte> findAllOrderByFechaDesc(Pageable pageable);
    
	List<MovimientoCtaCte> findByOperacion(Operacion operacion);

	List<MovimientoCtaCte> findAllByOrderByFechaDesc();

	
}
