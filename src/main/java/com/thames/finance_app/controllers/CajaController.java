package com.thames.finance_app.controllers;

import java.math.BigDecimal;
import java.sql.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.thames.finance_app.dtos.CajaDTO;
import com.thames.finance_app.dtos.MovimientoCajaDTO;
import com.thames.finance_app.models.Caja;
import com.thames.finance_app.services.CajaService;
import com.thames.finance_app.services.MovimientoCajaService;
import com.thames.finance_app.specifications.CajaSpecification;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/cajas")
@RequiredArgsConstructor
public class CajaController {

	private final CajaService cajaService;
	private final MovimientoCajaService movimientoCajaService;
	
	@GetMapping
	public String listarCajas(
			@RequestParam(required = false) String nombre,
			@RequestParam(required = false) String moneda,
			@RequestParam(required = false) String saldoMinimo,
			Model model,
			Pageable pageable) {
		
		BigDecimal saldoMinimoParsed = (saldoMinimo != null) ? new BigDecimal(saldoMinimo) : null ;
		
		Specification<Caja> spec = CajaSpecification
				.filtrarPorParametros(
						nombre, moneda, saldoMinimoParsed);
				
		
        Page<CajaDTO> cajas = cajaService.listarCajas(spec, pageable);
        model.addAttribute("cajas",cajas);
        return "cajas/lista";
    }
	
	@GetMapping("/movimientos")
	public String listarMovimientos(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) Date fechaInicio,
            @RequestParam(required = false) Date fechaFin,
            @RequestParam(required = false) BigDecimal monto,
            @RequestParam(required = false) String moneda,
            Pageable pageable,
            Model model) {
		
		Page<MovimientoCajaDTO> movimientos 
			= movimientoCajaService.obtenerMovimientosFiltrados(
                nombre, tipo, fechaInicio, fechaFin, monto, moneda, pageable);
		
		model.addAttribute("movimientos", movimientos);
        model.addAttribute("nombreTitular", nombre);
        model.addAttribute("tipo", tipo);
        model.addAttribute("fechaInicio", fechaInicio);
        model.addAttribute("fechaFin", fechaFin);
        model.addAttribute("moneda", moneda);
        
        return "cajas/lista";
    }
	
	@GetMapping("/crear")
	public String mostrarFormularioCrearCaja(Model model) {
		CajaDTO caja = new CajaDTO();
		model.addAttribute("caja",caja);
		return "cajas/crear";
	}
	
	@PostMapping("/guardar")
    public String crearCaja(@ModelAttribute CajaDTO cajaDto) {
		cajaService.crearCaja(cajaDto);
        return "redirect:/cajas";
    }
	
	@GetMapping("/movimientos/crear")
	public String mostrarFormularioCrearMovimiento(Model model) {
		MovimientoCajaDTO movimiento = new MovimientoCajaDTO();
		model.addAttribute("movimiento", movimiento);
		return "cajas/movimientos/crear";
	}
	
	
	@PostMapping("/movimientos/guardar")
	public String crearMovimiento(@ModelAttribute MovimientoCajaDTO dto) {
		cajaService.crearMovimiento(dto);
		return "cajas/movimientos";
	}
	
	@GetMapping("/{nombre}")
	public String verCaja(@PathVariable String nombre, Model model){
		CajaDTO caja = cajaService.obtenerPorNombre(nombre);
		model.addAttribute("caja", caja);
        return "cajas/ver";
	}
	
	@GetMapping("/{nombre}/editar")
	public String editarCaja() {
		return "redirect:/cajas";
	}
	 
	@DeleteMapping("/{id}/eliminar")
	public String eliminarCaja(@PathVariable Long id) {
		cajaService.eliminar(id);
        return "cajas";
	}	
	
}
