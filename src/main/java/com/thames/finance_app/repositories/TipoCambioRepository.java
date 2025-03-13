package com.thames.finance_app.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thames.finance_app.models.Moneda;
import com.thames.finance_app.models.TipoCambio;

@Repository
public interface TipoCambioRepository extends JpaRepository<TipoCambio, Long>{

	Optional<TipoCambio> findByMonedaOrigenAndMonedaDestino(Moneda monedaOrigen, Moneda monedaDestino);
	
}
