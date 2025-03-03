package com.thames.finance_app.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.thames.finance_app.dtos.OperacionRequest;
import com.thames.finance_app.dtos.OperacionResponse;
import com.thames.finance_app.enums.Moneda;
import com.thames.finance_app.enums.TipoOperacion;
import com.thames.finance_app.models.Operacion;
import com.thames.finance_app.services.OperacionService;
import com.thames.finance_app.specifications.OperacionSpecification;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;


@RestController
@RequestMapping("/operaciones")
@RequiredArgsConstructor
public class OperacionController {

    private final OperacionService operacionService;

    @PostMapping
    public ResponseEntity<OperacionResponse> crearOperacion(@Valid @RequestBody OperacionRequest request) {
        OperacionResponse response = operacionService.crearOperacion(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OperacionResponse> obtenerOperacion(@PathVariable Long id) {
        OperacionResponse response = operacionService.obtenerOperacion(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OperacionResponse> actualizarOperacion(@Valid @PathVariable Long id, @RequestBody OperacionRequest request) {
        OperacionResponse response = operacionService.actualizarOperacion(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarOperacion(@PathVariable Long id) {
        operacionService.eliminarOperacion(id);
        return ResponseEntity.noContent().build();
    }
    

    public ResponseEntity<Page<OperacionResponse>> listarOperaciones(
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin,
            @RequestParam(required = false) String monto,
            @RequestParam(required = false) String moneda,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) Long clienteId,
            Pageable pageable) {

        // Parsear los parámetros
        LocalDateTime fechaInicioParsed = operacionService.parsearFecha(fechaInicio);
        LocalDateTime fechaFinParsed = operacionService.parsearFecha(fechaFin);
        BigDecimal montoParsed = (monto != null) ? new BigDecimal(monto) : null;
        Moneda monedaParsed = (moneda != null) ? Moneda.valueOf(moneda) : null;
        TipoOperacion tipoParsed = (tipo != null) ? TipoOperacion.valueOf(tipo) : null;

        // Crear la Specification usando OperacionSpecification
        Specification<Operacion> spec = OperacionSpecification.filtrarPorParametros(
            fechaInicioParsed,
            fechaFinParsed,
            montoParsed,
            monedaParsed,
            tipoParsed,
            clienteId
        );

        // Llamar al servicio con la Specification y el Pageable
        Page<OperacionResponse> operaciones = operacionService.listarOperaciones(spec, pageable);

        return ResponseEntity.ok(operaciones);
    }

}

