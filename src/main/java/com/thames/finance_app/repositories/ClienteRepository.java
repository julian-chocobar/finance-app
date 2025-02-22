package com.thames.finance_app.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.thames.finance_app.models.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    
    List<Cliente> findByEsReferidoFalse();  // clientes

    
    List<Cliente> findByEsReferidoTrue(); // referidos

  
    Optional<Cliente> findByIdAndEsReferidoFalse(Long id);   // por ID cliente

    
    Optional<Cliente> findByIdAndEsReferidoTrue(Long id); // por ID referido

}
