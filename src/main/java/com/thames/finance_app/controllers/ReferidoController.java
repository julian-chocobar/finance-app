package com.thames.finance_app.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thames.finance_app.dtos.TitularRequest;
import com.thames.finance_app.dtos.TitularResponse;
import com.thames.finance_app.services.ReferidoService;

@RestController
@RequestMapping("/referidos")
public class ReferidoController {
	
	@Autowired
	private ReferidoService referidoService;
	
	@GetMapping("/referidos")
	public ResponseEntity<List<TitularResponse>> obtenerTodos() {
        List<TitularResponse> referidos = referidoService.obtenerTodosReferidos();
        return ResponseEntity.ok(referidos);
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
