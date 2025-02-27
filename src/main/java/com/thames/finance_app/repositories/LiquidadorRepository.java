package com.thames.finance_app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thames.finance_app.models.Liquidador;

@Repository
public interface LiquidadorRepository extends JpaRepository<Liquidador, Long>{

}
