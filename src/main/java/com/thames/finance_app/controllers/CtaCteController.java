package com.thames.finance_app.controllers;

import java.math.BigDecimal;
import java.sql.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.thames.finance_app.dtos.CtaCteDTO;
import com.thames.finance_app.dtos.MovimientoCtaCteDTO;
import com.thames.finance_app.enums.TipoMovimiento;
import com.thames.finance_app.models.Moneda;
import com.thames.finance_app.services.CtaCteService;
import com.thames.finance_app.services.MovimientoCtaCteService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/ctas-ctes")
@RequiredArgsConstructor
public class CtaCteController {

    private final CtaCteService ctaCteService;
    private final MovimientoCtaCteService movimientoCtaCteService;
    
    @GetMapping("/{id}")
    public ResponseEntity<CtaCteDTO> obtenerCuentaCorriente(@PathVariable Long id) {
        CtaCteDTO response = ctaCteService.obtenerResponsePorId(id);
        return ResponseEntity.ok(response);
    } 
    
    @GetMapping("/movimientos/crear")
    public String mostrarFormularioCrearMovimiento(Model model) {
    	MovimientoCtaCteDTO movimiento = new MovimientoCtaCteDTO();
    	model.addAttribute("movimiento", movimiento);
    	return "ctas-ctes/movimientos/crear"; 	
    }
    
    @PostMapping("movimientos/guardar")
    public String crearMovimiento(@ModelAttribute MovimientoCtaCteDTO dto) {
    	ctaCteService.crearMovimiento(dto);
    	return "redirect:/ctas-ctes"; 	
    }
    
    @GetMapping("/movimientos")
    public String listarMovimientos(
            @RequestParam(required = false) String nombreTitular,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) Date fechaInicio,
            @RequestParam(required = false) Date fechaFin,
            @RequestParam(required = false) BigDecimal monto,
            @RequestParam(required = false) String moneda,
            Pageable pageable,
            Model model) {

        Page<MovimientoCtaCteDTO> movimientos 
        	= movimientoCtaCteService.obtenerMovimientosFiltrados(
                nombreTitular, tipo, fechaInicio, fechaFin, monto, moneda, pageable);
       
        model.addAttribute("movimientos", movimientos);
        model.addAttribute("nombreTitular", nombreTitular);
        model.addAttribute("tipo", tipo);
        model.addAttribute("fechaInicio", fechaInicio);
        model.addAttribute("fechaFin", fechaFin);
        model.addAttribute("moneda", moneda);

        return "ctas-ctes/movimientos";
    }
    
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<CtaCteDTO> obtenerCuentaPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(ctaCteService.obtenerResponsePorTitularId(clienteId));
    }
    
    @GetMapping("/referido/{referidoId}")
    public ResponseEntity<CtaCteDTO> obtenerCuentaPorReferido(@PathVariable Long referidoId) {
        return ResponseEntity.ok(ctaCteService.obtenerResponsePorTitularId(referidoId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCuenta(@PathVariable Long id) {
        ctaCteService.eliminarCuenta(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/actualizar-saldo")
    public ResponseEntity<CtaCteDTO> actualizarSaldos(
            @PathVariable Long id,
            @RequestParam BigDecimal monto,
            @RequestParam Moneda moneda,
            @RequestParam TipoMovimiento tipo) {
        return ResponseEntity.ok(ctaCteService.actualizarSaldos(id, monto, moneda, tipo));
    }
    
}

