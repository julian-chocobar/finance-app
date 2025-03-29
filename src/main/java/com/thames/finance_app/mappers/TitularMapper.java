package com.thames.finance_app.mappers;


import org.springframework.stereotype.Component;

import com.thames.finance_app.dtos.TitularRequest;
import com.thames.finance_app.dtos.TitularResponse;
import com.thames.finance_app.enums.TipoTitular;
import com.thames.finance_app.models.Titular;

@Component
public class TitularMapper {

	public Titular toEntityCliente(TitularRequest request) {
        return Titular.builder()
        		.tipo(TipoTitular.CLIENTE)
                .nombre(normalizar(request.getNombre()))
                .telefono(normalizar(request.getTelefono()))
                .email(normalizar(request.getEmail()))
                .direccion(normalizar(request.getDireccion()))
                .build();
    }

	public Titular toEntityReferido(TitularRequest request) {
        return Titular.builder()
        		.tipo((TipoTitular.REFERIDO))
                .nombre(normalizar(request.getNombre()))
                .telefono(normalizar(request.getTelefono()))
                .email(normalizar(request.getEmail()))
                .direccion(normalizar(request.getDireccion()))
                .build();
    }

	public TitularResponse toResponse(Titular cliente) {
//	    CuentaCorriente cuenta = cliente.getCuentaCorriente();

	    return TitularResponse.builder()
	            .id(cliente.getId())
	            .nombre(cliente.getNombre())
	            .telefono(cliente.getTelefono())
	            .email(cliente.getEmail())
	            .direccion(cliente.getDireccion())
//	            .cuentaCorrienteId(cuenta != null ? cuenta.getId() : null)
//	            .saldos(cuenta != null ? new HashMap<>(cuenta.getSaldos()) : new HashMap<>()) // Copia de los saldos para evitar modificaciones accidentales
	            .build();
	}

	// Método auxiliar para convertir "null" en null real
	private String normalizar(String valor) {
	    return (valor == null || valor.trim().isEmpty() || "null".equalsIgnoreCase(valor.trim())) ? null : valor;
	}

	public void updateEntity(Titular clienteExistente, TitularRequest clienteRequest) {

	}
}
