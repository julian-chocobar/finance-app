package com.thames.finance_app.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thames.finance_app.dtos.CajaDTO;
import com.thames.finance_app.services.CajaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/cajas")
@RequiredArgsConstructor
public class CajaController {

	private final CajaService cajaService;
	
	@GetMapping("/todas")
	public ResponseEntity<List<CajaDTO>> obtenerTodos() {
        List<CajaDTO> cajas = cajaService.obtenerTodas();
        return ResponseEntity.ok(cajas);
    }
	
	@GetMapping("/{id}")
	public ResponseEntity<CajaDTO> obtenerPorID(@PathVariable Long id){
		CajaDTO caja = cajaService.obtenerPorID(id);
		return ResponseEntity.ok(caja);	
	}

	@PostMapping
    public ResponseEntity<CajaDTO> crearCaja(@RequestBody CajaDTO cajaDto) {
		CajaDTO caja = cajaService.crearCaja(cajaDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(caja);
    }
	
	 
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminarCliente(@PathVariable Long id) {
		cajaService.eliminar(id);
		return ResponseEntity.noContent().build();
	}	
	
}
