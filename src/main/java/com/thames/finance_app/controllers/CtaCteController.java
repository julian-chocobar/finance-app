package com.thames.finance_app.controllers;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.thames.finance_app.dtos.CtaCteResponse;
import com.thames.finance_app.dtos.MovimientoCtaCteDTO;
import com.thames.finance_app.enums.TipoMovimiento;
import com.thames.finance_app.models.Moneda;
import com.thames.finance_app.services.CtaCteService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/ctas-ctes")
@RequiredArgsConstructor
public class CtaCteController {

    private final CtaCteService ctaCteService;
    
    @GetMapping("/{id}")
    public ResponseEntity<CtaCteResponse> obtenerCuentaCorriente(@PathVariable Long id) {
        CtaCteResponse response = ctaCteService.obtenerResponsePorId(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/movimientos")
    public ResponseEntity<Page<MovimientoCtaCteDTO>> obtenerMovimientos(
            @RequestParam(required = false) LocalDate fechaExacta,
            @RequestParam(required = false) LocalDate fechaDesde,
            @RequestParam(required = false) LocalDate fechaHasta,
            @RequestParam(required = false) BigDecimal monto,
            @RequestParam(required = false) Moneda moneda,
            @RequestParam(required = false) TipoMovimiento tipo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<MovimientoCtaCteDTO> movimientos = ctaCteService.obtenerTodosLosMovimientos(
            fechaExacta, fechaDesde, fechaHasta, monto, moneda, tipo, pageable);
        
        return ResponseEntity.ok(movimientos);
    }
    
    @GetMapping("/{id}/movimientos")
    public ResponseEntity<Page<MovimientoCtaCteDTO>> obtenerMovimientos(
            @PathVariable Long id,
            @RequestParam(required = false) LocalDate fechaExacta,
            @RequestParam(required = false) LocalDate fechaDesde,
            @RequestParam(required = false) LocalDate fechaHasta,
            @RequestParam(required = false) BigDecimal monto,
            @RequestParam(required = false) Moneda moneda,
            @RequestParam(required = false) TipoMovimiento tipo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<MovimientoCtaCteDTO> movimientos = ctaCteService.obtenerMovimientos(id, fechaExacta, fechaDesde, fechaHasta, monto, moneda, tipo, pageable);
        
        return ResponseEntity.ok(movimientos);
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<CtaCteResponse> obtenerCuentaPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(ctaCteService.obtenerResponsePorClienteId(clienteId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCuenta(@PathVariable Long id) {
        ctaCteService.eliminarCuenta(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/actualizar-saldo")
    public ResponseEntity<CtaCteResponse> actualizarSaldos(
            @PathVariable Long id,
            @RequestParam BigDecimal monto,
            @RequestParam Moneda moneda,
            @RequestParam TipoMovimiento tipo) {
        return ResponseEntity.ok(ctaCteService.actualizarSaldos(id, monto, moneda, tipo));
    }
    
}

