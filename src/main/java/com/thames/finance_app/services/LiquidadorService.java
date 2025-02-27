package com.thames.finance_app.services;

import com.thames.finance_app.dtos.LiquidadorRequest;
import com.thames.finance_app.mappers.LiquidadorMapper;
import com.thames.finance_app.models.Liquidador;
import com.thames.finance_app.repositories.LiquidadorRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LiquidadorService {

    private final LiquidadorRepository liquidadorRepository;
    private final LiquidadorMapper liquidadorMapper;

    public Liquidador crearLiquidador(LiquidadorRequest request) {
        Liquidador liquidador = liquidadorMapper.toEntity(request);
        return liquidadorRepository.save(liquidador);
    }

    public Liquidador obtenerPorId(Long id) {
        return liquidadorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Liquidador no encontrado"));
    }

    public List<Liquidador> listarTodos() {
        return liquidadorRepository.findAll();
    }

    public Liquidador actualizarLiquidador(Long id, LiquidadorRequest request) {
        Liquidador liquidador = obtenerPorId(id);
        liquidador.setNombre(request.getNombre());
        return liquidadorRepository.save(liquidador);
    }

    public void eliminarLiquidador(Long id) {
        liquidadorRepository.deleteById(id);
    }
}

