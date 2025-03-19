package com.thames.finance_app.controllers;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.thames.finance_app.dtos.TitularRequest;
import com.thames.finance_app.dtos.TitularResponse;
import com.thames.finance_app.services.ReferidoService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/referidos")
@RequiredArgsConstructor
public class ReferidoController {
	
	private final ReferidoService referidoService;
		
    @GetMapping
    public String obtenerTodos(@RequestParam(required = false) String nombre, Pageable pageable, Model model) {
        Page<TitularResponse> referidos = (nombre == null || nombre.isEmpty())
                ? referidoService.obtenerTodosReferidos(pageable)
                : referidoService.buscarReferidosPorNombre(nombre, pageable);
        model.addAttribute("referidos", referidos);
        model.addAttribute("nombreFiltro", nombre);
        return "referidos";
    }
	
	@GetMapping("/{id}")
	public ResponseEntity<TitularResponse> obtenerPorID(@PathVariable Long id){
		TitularResponse referido = referidoService.obtenerReferidoPorID(id);
		return ResponseEntity.ok(referido);	
	}
	
	@PostMapping
	public String crearReferido(@ModelAttribute TitularRequest referidoRequest) {
	    referidoService.crearReferido(referidoRequest);
	    return "redirect:/referidos"; // Redirige a la lista de referidos después de la creación
	}
	
	@PostMapping("/editar/{id}")
	public String actuslizarReferido(@PathVariable Long id, @ModelAttribute TitularRequest referidoRequest) {
	    referidoService.actualizar(id, referidoRequest);
	    return "redirect:/referidos";
	}
		
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminarReferido(@PathVariable Long id) {
	    referidoService.eliminar(id);
	    return ResponseEntity.ok().build(); // Retorna un código 200 (OK)
	}

}
