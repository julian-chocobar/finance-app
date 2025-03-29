package com.thames.finance_app.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thames.finance_app.dtos.MonedaDTO;
import com.thames.finance_app.services.MonedaService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/monedas")
@RequiredArgsConstructor
public class MonedaController {

	private final MonedaService monedaService;

    @PostMapping
    public ResponseEntity<MonedaDTO> crearMoneda(@RequestBody MonedaDTO monedaDTO) {
        MonedaDTO nuevaMoneda = monedaService.crearMoneda(monedaDTO);
        return new ResponseEntity<>(nuevaMoneda, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MonedaDTO> obtenerMonedaPorId(@PathVariable Long id) {
        MonedaDTO monedaDTO = monedaService.obtenerMonedaPorId(id);
        return new ResponseEntity<>(monedaDTO, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<MonedaDTO>> listarTodasLasMonedas() {
        List<MonedaDTO> monedas = monedaService.listarTodasLasMonedas();
        return new ResponseEntity<>(monedas, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MonedaDTO> actualizarMoneda(
            @PathVariable Long id,
            @RequestBody MonedaDTO monedaDTO) {
        MonedaDTO monedaActualizada = monedaService.actualizarMoneda(id, monedaDTO);
        return new ResponseEntity<>(monedaActualizada, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarMoneda(@PathVariable Long id) {
        monedaService.eliminarMoneda(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
