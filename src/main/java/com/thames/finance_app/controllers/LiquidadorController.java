package com.thames.finance_app.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.thames.finance_app.dtos.LiquidadorRequest;
import com.thames.finance_app.dtos.LiquidadorResponse;
import com.thames.finance_app.mappers.LiquidadorMapper;
import com.thames.finance_app.models.Liquidador;
import com.thames.finance_app.services.LiquidadorService;

import java.util.List;

@RestController
@RequestMapping("/liquidadores")
@RequiredArgsConstructor
public class LiquidadorController {

    private final LiquidadorService liquidadorService;
    private final LiquidadorMapper liquidadorMapper;

    @PostMapping
    public ResponseEntity<LiquidadorResponse> crearLiquidador(@RequestBody LiquidadorRequest request) {
        Liquidador liquidador = liquidadorService.crearLiquidador(request);
        return ResponseEntity.ok(liquidadorMapper.toResponse(liquidador));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LiquidadorResponse> obtenerLiquidador(@PathVariable Long id) {
        Liquidador liquidador = liquidadorService.obtenerPorId(id);
        return ResponseEntity.ok(liquidadorMapper.toResponse(liquidador));
    }

    @GetMapping
    public ResponseEntity<List<LiquidadorResponse>> listarLiquidadores() {
        List<Liquidador> liquidadores = liquidadorService.listarTodos();
        List<LiquidadorResponse> responseList = liquidadores.stream()
                .map(liquidadorMapper::toResponse)
                .toList();
        return ResponseEntity.ok(responseList);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LiquidadorResponse> actualizarLiquidador(@PathVariable Long id, @RequestBody LiquidadorRequest request) {
        Liquidador liquidador = liquidadorService.actualizarLiquidador(id, request);
        return ResponseEntity.ok(liquidadorMapper.toResponse(liquidador));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarLiquidador(@PathVariable Long id) {
        liquidadorService.eliminarLiquidador(id);
        return ResponseEntity.noContent().build();
    }
}
