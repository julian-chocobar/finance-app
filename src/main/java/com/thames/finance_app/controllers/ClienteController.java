package com.thames.finance_app.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
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

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class ClienteController {
	
	private final ClienteService clienteService;
	private final PagedResourcesAssembler<TitularResponse> pagedResourcesAssembler;
	
	@GetMapping("/listado")
	public ResponseEntity<org.springframework.hateoas.PagedModel<EntityModel<TitularResponse>>> obtenerTodos(Pageable pageable) {
        Page<TitularResponse> clientes = clienteService.obtenerTodos(pageable);        
	    org.springframework.hateoas.PagedModel<EntityModel<TitularResponse>> pagedModel = pagedResourcesAssembler.toModel(clientes);
        return ResponseEntity.ok(pagedModel);
    }
	
	@GetMapping("/{id}")
	public ResponseEntity<TitularResponse> obtenerPorID(@PathVariable Long id){
		TitularResponse cliente = clienteService.obtenerPorID(id);
		return ResponseEntity.ok(cliente);	
	}
	
	@PostMapping
    public ResponseEntity<TitularResponse> crearCliente(@RequestBody TitularRequest clienteRequest) {
        TitularResponse clienteResponse = clienteService.crear(clienteRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteResponse);
    }
	
	 @PutMapping("/{id}")
	 public ResponseEntity<TitularResponse> actualizarCliente(@PathVariable Long id, @RequestBody TitularRequest clienteRequest){
		 return ResponseEntity.ok(clienteService.actualizar(id, clienteRequest));
	 }
	 
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminarCliente(@PathVariable Long id) {
		clienteService.eliminar(id);
	    return ResponseEntity.noContent().build();
	}	 
	
}
