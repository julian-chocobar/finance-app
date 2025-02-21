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

import com.thames.finance_app.dtos.ClienteRequest;
import com.thames.finance_app.dtos.ClienteResponse;
import com.thames.finance_app.services.ClienteService;


@RestController
@RequestMapping("/clientes")
public class ClienteController {
	
	@Autowired
	private ClienteService clienteService;
	
	@GetMapping("/clientes")
	public ResponseEntity<List<ClienteResponse>> obtenerTodos() {
        List<ClienteResponse> products = clienteService.obtenerTodos();
        return ResponseEntity.ok(products);
    }
	
	@GetMapping("/clientes")
	public ResponseEntity<List<ClienteResponse>> obtenerTodosClientes() {
        List<ClienteResponse> products = clienteService.obtenerTodosClientes();
        return ResponseEntity.ok(products);
    }
	
	@GetMapping("/referidos")
	public ResponseEntity<List<ClienteResponse>> obtenerTodosReferidos() {
        List<ClienteResponse> products = clienteService.obtenerTodosReferidos();
        return ResponseEntity.ok(products);
    }
	
	@GetMapping("/{id}")
	public ResponseEntity<ClienteResponse> obtenerPorID(@PathVariable Long id){
		ClienteResponse cliente = clienteService.obtenerPorID(id);
		return ResponseEntity.ok(cliente);	
	}
	
	@PostMapping
    public ResponseEntity<ClienteResponse> crearCliente(@RequestBody ClienteRequest clienteRequest) {
        ClienteResponse clienteResponse = clienteService.crearCliente(clienteRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteResponse);
    }
	
	 @PutMapping("/{id}")
	 public ResponseEntity<ClienteResponse> actualizarCliente(@PathVariable Long id, @RequestBody ClienteRequest clienteRequest) {
		 return ResponseEntity.ok(clienteService.actualizarCliente(id, clienteRequest));
	 }

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminarCliente(@PathVariable Long id) {
		clienteService.eliminarCliente(id);
	    return ResponseEntity.noContent().build();
	}	 
	
	
	
}
