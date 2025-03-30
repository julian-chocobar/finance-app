package com.thames.finance_app.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;


import com.thames.finance_app.models.Moneda;
import com.thames.finance_app.models.TipoCambio;

@Repository
public interface TipoCambioRepository extends JpaRepository<TipoCambio, Long>{

	Optional<TipoCambio> findByMonedaOrigenAndMonedaConversion(Moneda monedaOrigen, Moneda monedaConversion);

	@NonNull
	Page<TipoCambio> findAll(@NonNull Pageable pageable);
}
