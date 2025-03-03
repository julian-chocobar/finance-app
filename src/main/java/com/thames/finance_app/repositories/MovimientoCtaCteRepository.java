package com.thames.finance_app.repositories;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.thames.finance_app.enums.Moneda;
import com.thames.finance_app.enums.TipoMovimiento;
import com.thames.finance_app.models.MovimientoCtaCte;


@Repository
public interface MovimientoCtaCteRepository  extends JpaRepository<MovimientoCtaCte, Long>{

	List<MovimientoCtaCte> findByCuentaCorrienteId(Long cuentaId);
	
	

//	Page<MovimientoCtaCte> filtrarMovimientos(Long cuentaId, LocalDate fechaDesde, LocalDate fechaHasta,
//			LocalDate fechaExacta, BigDecimal monto, Moneda moneda, TipoMovimiento tipo, Pageable pageable);
	

	    @Query("SELECT m FROM MovimientoCtaCte m " +
	           "WHERE (:cuentaCorrienteId IS NULL OR m.cuentaCorriente.id = :cuentaCorrienteId) " +
	           "AND (:fechaExacta IS NULL OR m.fecha = :fechaExacta) " +  // Filtro por fecha exacta
	           "AND (:fechaDesde IS NULL OR m.fecha >= :fechaDesde) " +
	           "AND (:fechaHasta IS NULL OR m.fecha <= :fechaHasta) " +
	           "AND (:monto IS NULL OR m.monto = :monto) " +
	           "AND (:moneda IS NULL OR m.moneda = :moneda) " +
	           "AND (:tipoMovimiento IS NULL OR m.tipoMovimiento = :tipoMovimiento)")
	    Page<MovimientoCtaCte> filtrarMovimientos(
	        @Param("cuentaCorrienteId") Long cuentaCorrienteId,
	        @Param("fechaExacta") LocalDate fechaExacta,  // Nuevo parámetro para fecha exacta
	        @Param("fechaDesde") LocalDate fechaDesde,
	        @Param("fechaHasta") LocalDate fechaHasta,
	        @Param("monto") BigDecimal monto,
	        @Param("moneda") Moneda moneda,
	        @Param("tipoMovimiento") TipoMovimiento tipoMovimiento,
	        Pageable pageable);
	

	
}
