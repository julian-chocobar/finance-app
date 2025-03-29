package com.thames.finance_app.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.thames.finance_app.dtos.TitularResponse;
import com.thames.finance_app.enums.TipoTitular;
import com.thames.finance_app.models.Titular;

public interface TitularRepository extends JpaRepository<Titular, Long> {


    Optional <Titular> findByNombre(String name);

    Page<Titular> findByTipo(TipoTitular tipo, Pageable pageable);

	Optional<Titular> findByNombreAndTipo(String nombre, TipoTitular tipo);

	Page<TitularResponse> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);

}
