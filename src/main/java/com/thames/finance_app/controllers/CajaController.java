package com.thames.finance_app.controllers;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
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
import com.thames.finance_app.models.MovimientoCaja;
import com.thames.finance_app.services.CajaService;
import com.thames.finance_app.services.MovimientoCajaService;
import com.thames.finance_app.specifications.CajaSpecification;
import com.thames.finance_app.specifications.MovimientoCajaSpecification;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/cajas")
@RequiredArgsConstructor
public class CajaController {

	private final CajaService cajaService;
	private final MovimientoCajaService movimientoCajaService;
	
    @GetMapping
    public String listarCajas(Model model, 
                             @RequestParam(required = false) String nombre,
                             @RequestParam(required = false) String moneda,
                             @RequestParam(required = false) String saldoMinimo,
                             @RequestParam(required = false) String nombreCaja,
                             @RequestParam(required = false) String fechaDesde,
                             @RequestParam(required = false) String fechaHasta,
                             @RequestParam(required = false) String tipo,
                             @RequestParam(required = false) String montoMinimo,
                             @RequestParam(required = false) String montoMaximo,
                             @RequestParam(required = false) String idOperacion,
                             @Qualifier("cajasPageable") @PageableDefault(size = 10) Pageable cajasPageable,
                             @Qualifier("movimientosPageable") @PageableDefault(size = 20) Pageable movimientosPageable) {
        
        // Parsear datos de caja
        BigDecimal saldoMinimoParsed = (saldoMinimo != null && !saldoMinimo.isEmpty()) ? new BigDecimal(saldoMinimo) : null;
        
        // Obtener cajas paginadas
        Specification<Caja> specCaja = CajaSpecification
                .filtrarPorParametros(nombre, moneda, saldoMinimoParsed);    
        Page<CajaDTO> cajas = cajaService.listarCajas(specCaja, cajasPageable);
        model.addAttribute("cajas", cajas);
        
        // Parsear datos de movimientos
        Long idOperacionParsed = (idOperacion != null && !idOperacion.isEmpty()) ? Long.parseLong(idOperacion) : null;
        BigDecimal montoMinimoParsed = (montoMinimo != null && !montoMinimo.isEmpty()) ? new BigDecimal(montoMinimo) : null;
        BigDecimal montoMaximoParsed = (montoMaximo != null && !montoMaximo.isEmpty()) ? new BigDecimal(montoMaximo) : null;
        LocalDateTime fechaInicioParsed = movimientoCajaService.parsearFecha(fechaDesde);
        LocalDateTime fechaFinParsed = movimientoCajaService.parsearFecha(fechaHasta);
        
        // Obtener movimientos paginados
        Specification<MovimientoCaja> specMovimiento = MovimientoCajaSpecification
                .filtrarMovimientos(nombreCaja, tipo, fechaInicioParsed, fechaFinParsed, 
                                  montoMinimoParsed, montoMaximoParsed, idOperacionParsed);    
        Page<MovimientoCajaDTO> movimientos = movimientoCajaService.obtenerMovimientosFiltrados(specMovimiento, movimientosPageable);
        
        model.addAttribute("movimientos", movimientos);
		model.addAttribute("fechaDesde", fechaDesde);
		model.addAttribute("fechaHasta", fechaHasta);	
		model.addAttribute("tipo", tipo);
		model.addAttribute("montoMinimo", montoMinimo);
		model.addAttribute("montoMaximo", montoMaximo);
		model.addAttribute("idOperacion", idOperacion);
		model.addAttribute("nombreCaja", nombreCaja);	
		model.addAttribute("moneda", moneda);	
		model.addAttribute("saldoMinimo", saldoMinimo);		
        
        return "cajas/lista";
    }
	
	@GetMapping("/{nombre}")
	public String verCaja(
		@PathVariable String nombre,
		@RequestParam(required = false) String fechaDesde,
		@RequestParam(required = false) String fechaHasta,
		@RequestParam(required = false) String tipo,
        @RequestParam(required = false) String montoMinimo,
        @RequestParam(required = false) String montoMaximo,
		@RequestParam(required = false) String idOperacion,
		Pageable pageable,
		Model model){
		
		CajaDTO caja = cajaService.obtenerPorNombre(nombre);
		
		Long idOperacionParsed = (idOperacion != null && !idOperacion.isEmpty()) ? Long.parseLong(idOperacion) : null;
		BigDecimal montoMinimoParsed = (montoMinimo != null && !montoMinimo.isEmpty()) ? new BigDecimal(montoMinimo) : null;
		BigDecimal montoMaximoParsed = (montoMaximo != null && !montoMaximo.isEmpty()) ? new BigDecimal(montoMaximo) : null;
        LocalDateTime fechaInicioParsed = movimientoCajaService.parsearFecha(fechaDesde);
        LocalDateTime fechaFinParsed = movimientoCajaService.parsearFecha(fechaHasta);
		
		Specification<MovimientoCaja> specMovimiento = MovimientoCajaSpecification
				.filtrarMovimientos(
						nombre, tipo, fechaInicioParsed, fechaFinParsed, montoMinimoParsed, montoMaximoParsed, idOperacionParsed);	
				
        Page<MovimientoCajaDTO> movimientos = movimientoCajaService.obtenerMovimientosFiltrados(specMovimiento, pageable);
		
		model.addAttribute("caja", caja);
        model.addAttribute("movimientos", movimientos);
        return "cajas/ver";
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
		
	@GetMapping("/{nombre}/editar")
    public String mostrarFormularioEditar(@PathVariable String nombre, Model model) {
		CajaDTO caja = cajaService.obtenerPorNombre(nombre);
		model.addAttribute("caja", caja);
		return "cajas/editar";		
    }
	
	@PostMapping("/{nombre}/actualizar")
	public String editarCaja(@PathVariable String nombre, @ModelAttribute CajaDTO cajaDTO) {
		Caja caja = cajaService.obtenerEntidadPorNombre(nombre);
		cajaService.actualizarCaja(caja.getId(), cajaDTO);
		return "redirect:/cajas";
	}
	 
	@DeleteMapping("/{nombre}/eliminar")
	public String eliminarCaja(@PathVariable Long id) {
		Caja caja = cajaService.obtenerEntidadPorId(id);
		cajaService.eliminar(caja.getId());
        return "cajas";
	}
}
