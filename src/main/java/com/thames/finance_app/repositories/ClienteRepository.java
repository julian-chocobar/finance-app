package com.thames.finance_app.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.thames.finance_app.models.Titular;

public interface ClienteRepository extends JpaRepository<Titular, Long> {

    
    List<Titular> findByEsReferidoFalse();  // clientes

    
    List<Titular> findByEsReferidoTrue(); // referidos

  
    Optional<Titular> findByIdAndEsReferidoFalse(Long id);   // por ID cliente

    Optional<Titular> findByIdAndEsReferidoTrue(Long id); // por ID referido
    
    Optional <Titular> findByNombre(String name);

}
