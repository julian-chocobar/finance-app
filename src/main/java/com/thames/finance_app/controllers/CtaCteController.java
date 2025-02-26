package com.thames.finance_app.controllers;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.thames.finance_app.dtos.CtaCteRequest;
import com.thames.finance_app.dtos.CtaCteResponse;
import com.thames.finance_app.dtos.MovimientoCtaCteResponse;
import com.thames.finance_app.enums.Moneda;
import com.thames.finance_app.enums.TipoMovimiento;
import com.thames.finance_app.services.CtaCteService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/cuentas-corrientes")
@RequiredArgsConstructor
public class CtaCteController {

    private final CtaCteService ctaCteService;
    
    @GetMapping("/{id}")
    public ResponseEntity<CtaCteResponse> obtenerCuentaCorriente(@PathVariable Long id) {
        CtaCteResponse response = ctaCteService.obtenerCuentaPorId(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}/movimientos")
    public ResponseEntity<Page<MovimientoCtaCteResponse>> obtenerMovimientos(
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
        Page<MovimientoCtaCteResponse> movimientos = ctaCteService.obtenerMovimientos(id, fechaExacta, fechaDesde, fechaHasta, monto, moneda, tipo, pageable);
        
        return ResponseEntity.ok(movimientos);
    }


    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<CtaCteResponse> obtenerCuentaPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(ctaCteService.obtenerCuentaPorClienteId(clienteId));
    }

    @PostMapping
    public ResponseEntity<CtaCteResponse> crearCuenta(@RequestBody CtaCteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ctaCteService.crearCuentaCorriente(request));
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

