package com.thames.finance_app.mappers;

import java.util.HashMap;

import org.springframework.stereotype.Component;

import com.thames.finance_app.dtos.ClienteRequest;
import com.thames.finance_app.dtos.ClienteResponse;
import com.thames.finance_app.models.Cliente;
import com.thames.finance_app.models.CuentaCorriente;

@Component
public class ClienteMapper {
	
	public Cliente toEntity(ClienteRequest request) {
        return Cliente.builder()
        		.esReferido(false)
                .nombre(request.getNombre())
                .telefono(request.getTelefono())
                .email(request.getEmail())
                .direccion(request.getDireccion())
                .build();
    }
	
	public Cliente toEntityReferido(ClienteRequest request) {
        return Cliente.builder()
        		.esReferido(true)
                .nombre(request.getNombre())
                .telefono(request.getTelefono())
                .email(request.getEmail())
                .direccion(request.getDireccion())
                .build();
    }

	public ClienteResponse toResponse(Cliente cliente) {
	    CuentaCorriente cuenta = cliente.getCuentaCorriente();

	    return ClienteResponse.builder()
	            .id(cliente.getId())
	            .nombre(cliente.getNombre())
	            .telefono(cliente.getTelefono())
	            .email(cliente.getEmail())
	            .direccion(cliente.getDireccion())
	            .cuentaCorrienteId(cuenta != null ? cuenta.getId() : null)
	            .saldos(cuenta != null ? new HashMap<>(cuenta.getSaldos()) : new HashMap<>()) // Copia de los saldos para evitar modificaciones accidentales
	            .build();
	}


	public void updateEntity(Cliente clienteExistente, ClienteRequest clienteRequest) {
			
	}
}
