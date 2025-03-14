package com.thames.finance_app.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.thames.finance_app.enums.TipoTitular;
import com.thames.finance_app.models.Titular;

public interface TitularRepository extends JpaRepository<Titular, Long> {

        
    Optional <Titular> findByNombre(String name);
    
    List<Titular> findByTipo(TipoTitular tipo);

	Optional<Titular> findByNombreAndTipo(String nombre, TipoTitular tipo);

}
