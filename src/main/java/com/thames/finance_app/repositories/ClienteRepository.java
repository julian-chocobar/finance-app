package com.thames.finance_app.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.thames.finance_app.models.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

	List<Cliente> findAllClientes();

	List<Cliente> findAllReferidos();

}
