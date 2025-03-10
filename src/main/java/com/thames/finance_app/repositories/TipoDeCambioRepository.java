package com.thames.finance_app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thames.finance_app.enums.Moneda;
import com.thames.finance_app.models.TipoDeCambio;

@Repository
public interface TipoDeCambioRepository extends JpaRepository<TipoDeCambio, Long> {

	TipoDeCambio findByMoneda(Moneda moneda);

}
