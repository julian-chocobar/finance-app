package com.thames.finance_app.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

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
    public String obtenerTodos(Pageable pageable, Model model) {
        Page<TitularResponse> referidos = referidoService.obtenerTodosReferidos(pageable);
        model.addAttribute("referidos", referidos);
        return "referidos"; // Nombre de la vista Thymeleaf (referidos.html)
    }

	
	@GetMapping("/{id}")
	public ResponseEntity<TitularResponse> obtenerPorID(@PathVariable Long id){
		TitularResponse referido = referidoService.obtenerReferidoPorID(id);
		return ResponseEntity.ok(referido);	
	}
	
	@PostMapping("/referidos")
	public ResponseEntity<TitularResponse> crearReferido(@RequestBody TitularRequest rederidoRequest) {
	    TitularResponse referidoResponse = referidoService.crearReferido(rederidoRequest);
	    return ResponseEntity.status(HttpStatus.CREATED).body(referidoResponse);
	}
	
	 @PutMapping("/{id}")
	 public ResponseEntity<TitularResponse> actualizarReferido(@PathVariable Long id, @RequestBody TitularRequest rederidoRequest) {
		 return ResponseEntity.ok(referidoService.actualizar(id, rederidoRequest));
	 }

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminarReferido(@PathVariable Long id) {
		referidoService.eliminar(id);
	    return ResponseEntity.noContent().build();
	}	 


}
