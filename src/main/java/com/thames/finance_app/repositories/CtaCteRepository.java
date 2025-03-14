package com.thames.finance_app.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thames.finance_app.models.Titular;
import com.thames.finance_app.models.CuentaCorriente;

@Repository
public interface CtaCteRepository extends JpaRepository<CuentaCorriente, Long> {

    Optional<CuentaCorriente> findByTitular(Titular titular);

    boolean existsByTitular(Titular cliente);
}
