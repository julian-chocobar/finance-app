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
import com.thames.finance_app.services.ClienteService;


@RestController
@RequestMapping("/clientes")
public class ClienteController {
	
	@Autowired
	private ClienteService clienteService;
	
	@GetMapping("/todos")
	public ResponseEntity<List<TitularResponse>> obtenerTodos() {
        List<TitularResponse> products = clienteService.obtenerTodosClientes();
        return ResponseEntity.ok(products);
    }
	
	@GetMapping("/{id}")
	public ResponseEntity<TitularResponse> obtenerPorID(@PathVariable Long id){
		TitularResponse cliente = clienteService.obtenerClientePorID(id);
		return ResponseEntity.ok(cliente);	
	}
	
	@PostMapping
    public ResponseEntity<TitularResponse> crearCliente(@RequestBody TitularRequest clienteRequest) {
        TitularResponse clienteResponse = clienteService.crearCliente(clienteRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteResponse);
    }
	
	 @PutMapping("/{id}")
	 public ResponseEntity<TitularResponse> actualizarCliente(@PathVariable Long id, @RequestBody TitularRequest clienteRequest){
		 return ResponseEntity.ok(clienteService.actualizarCliente(id, clienteRequest));
	 }
	 
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminarCliente(@PathVariable Long id) {
		clienteService.eliminarCliente(id);
	    return ResponseEntity.noContent().build();
	}	 
	
}
