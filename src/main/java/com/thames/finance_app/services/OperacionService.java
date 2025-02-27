package com.thames.finance_app.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.thames.finance_app.dtos.OperacionRequest;
import com.thames.finance_app.dtos.OperacionResponse;
import com.thames.finance_app.mappers.OperacionMapper;
import com.thames.finance_app.models.Operacion;
import com.thames.finance_app.repositories.OperacionRepository;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OperacionService {

    private final OperacionRepository operacionRepository;
    private final CtaCteService cuentaCorrienteService;
    private final LiquidadorService liquidadorService;
    private final OperacionMapper operacionMapper;

    public OperacionResponse crearOperacion(OperacionRequest request) {
        Operacion operacion = operacionMapper.toEntity(request, 
        									request.getCuentaCorrienteId(), 
        									request.getCuentaCorrienteReferidoId(), 
        									request.getLiquidadorId());
        operacion.setFecha(LocalDateTime.now());
        operacion.setCuentaCorriente(cuentaCorrienteService.obtenerPorId(request.getCuentaCorrienteId()));
        if (request.getCuentaCorrienteReferidoId() != null) {
            operacion.setCuentaCorrienteReferido(cuentaCorrienteService.obtenerPorId(request.getCuentaCorrienteReferidoId()));
        }
        if (request.getLiquidadorId() != null) {
            operacion.setLiquidador(liquidadorService.obtenerPorId(request.getLiquidadorId()));
        }
        operacion = operacionRepository.save(operacion);
        return operacionMapper.toResponse(operacion);
    }

    public OperacionResponse obtenerOperacion(Long id) {
        Optional<Operacion> operacion = operacionRepository.findById(id);
        return operacion.map(operacionMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Operación no encontrada"));
    }

    public OperacionResponse actualizarOperacion(Long id, OperacionRequest request) {
        Operacion operacion = operacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Operación no encontrada"));

        operacionMapper.updateEntity(operacion, request);
        if (request.getCuentaCorrienteReferidoId() != null) {
            operacion.setCuentaCorrienteReferido(cuentaCorrienteService.obtenerPorId(request.getCuentaCorrienteReferidoId()));
        }
        if (request.getLiquidadorId() != null) {
            operacion.setLiquidador(liquidadorService.obtenerPorId(request.getLiquidadorId()));
        }

        operacion = operacionRepository.save(operacion);
        return operacionMapper.toResponse(operacion);
    }

    public void eliminarOperacion(Long id) {
        operacionRepository.deleteById(id);
    }

    public Page<OperacionResponse> listarOperaciones(Specification<Operacion> spec, Pageable pageable) {
        return operacionRepository.findAll(spec, pageable).map(operacionMapper::toResponse);
    }
}


