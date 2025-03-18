package com.thames.finance_app.controllers;

import java.math.BigDecimal;
import java.sql.Date;

import org.springframework.data.domain.Page;
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
import com.thames.finance_app.services.MovimientoCtaCteService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/ctas-ctes")
@RequiredArgsConstructor
public class CtaCteController {

    private final CtaCteService ctaCteService;
    private final MovimientoCtaCteService movimientoCtaCteService;
    
    @GetMapping("/{id}")
    public ResponseEntity<CtaCteResponse> obtenerCuentaCorriente(@PathVariable Long id) {
        CtaCteResponse response = ctaCteService.obtenerResponsePorId(id);
        return ResponseEntity.ok(response);
    }    
    
    @GetMapping("/movimientos")
    public ResponseEntity<Page<MovimientoCtaCteDTO>> listarMovimientos(
            @RequestParam(required = false) String nombreTitular,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) Date fechaDesde,
            @RequestParam(required = false) Date fechaHasta,
            @RequestParam(required = false) BigDecimal monto,
            @RequestParam(required = false) String moneda,
            Pageable pageable) {

        Page<MovimientoCtaCteDTO> movimientos = movimientoCtaCteService.obtenerMovimientosFiltrados(
                nombreTitular, tipo, fechaDesde, fechaHasta, monto, moneda, pageable);
        
        return ResponseEntity.ok(movimientos);
    }
    
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<CtaCteResponse> obtenerCuentaPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(ctaCteService.obtenerResponsePorTitularId(clienteId));
    }
    
    @GetMapping("/referido/{referidoId}")
    public ResponseEntity<CtaCteResponse> obtenerCuentaPorReferido(@PathVariable Long referidoId) {
        return ResponseEntity.ok(ctaCteService.obtenerResponsePorTitularId(referidoId));
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

