package com.thames.finance_app.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thames.finance_app.models.Moneda;

@Repository
public interface MonedaRepository extends JpaRepository<Moneda, Long>{

	Optional <Moneda> findByNombre(String nombre);

}
