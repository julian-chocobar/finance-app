package com.thames.finance_app.repositories;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thames.finance_app.enums.Moneda;
import com.thames.finance_app.enums.TipoMovimiento;
import com.thames.finance_app.models.MovimientoCtaCte;


@Repository
public interface MovimientoCtaCteRepository  extends JpaRepository<MovimientoCtaCte, Long>{

	List<MovimientoCtaCte> findByCuentaCorrienteId(Long cuentaId);

	Page<MovimientoCtaCte> filtrarMovimientos(Long cuentaId, LocalDate fechaDesde, LocalDate fechaHasta,
			LocalDate fechaExacta, BigDecimal monto, Moneda moneda, TipoMovimiento tipo, Pageable pageable);

	
}
