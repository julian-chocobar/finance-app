package com.thames.finance_app.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thames.finance_app.dtos.ClienteResponse;
import com.thames.finance_app.models.Operacion;

@Repository
public interface OperacionRepository extends JpaRepository<Operacion, Long>{

	List<ClienteResponse> findByCuentaCorriente_ClienteId(Long id);

}
