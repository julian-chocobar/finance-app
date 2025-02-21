package com.thames.finance_app.services;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.thames.finance_app.dtos.ClienteRequest;
import com.thames.finance_app.dtos.ClienteResponse;
import com.thames.finance_app.mappers.ClienteMapper;
import com.thames.finance_app.models.Cliente;
import com.thames.finance_app.models.CuentaCorriente;
import com.thames.finance_app.repositories.ClienteRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class ClienteService {
	
	private final ClienteRepository clienteRepository;
	private final ClienteMapper clienteMapper;
	
	public ClienteService (ClienteRepository clienteRepository, ClienteMapper clienteMapper) {
		this.clienteRepository = clienteRepository;
		this.clienteMapper = clienteMapper;
	}
	
	public List<ClienteResponse> obtenerTodos(){
		List<Cliente> clientes = clienteRepository.findAll();
		return clientes.stream()
				.map(clienteMapper::toResponse)
				.collect(Collectors.toList());	
	}
	
	public List<ClienteResponse> obtenerTodosClientes(){
		List<Cliente> clientes = clienteRepository.findAllClientes();
		return clientes.stream()
				.map(clienteMapper::toResponse)
				.collect(Collectors.toList());	
	}
	
	public List<ClienteResponse> obtenerTodosReferidos(){
		List<Cliente> referidos = clienteRepository.findAllReferidos();
		return referidos.stream()
				.map(clienteMapper::toResponse)
				.collect(Collectors.toList());	
	}
	
	public ClienteResponse obtenerPorID(Long id) {
		Cliente cliente = clienteRepository.findById(id)
				.orElseThrow( () -> new EntityNotFoundException("Cliente con id: " + id + " no encontrado"));
		return clienteMapper.toResponse(cliente);	
	}
	
	@Transactional
	public ClienteResponse crearCliente(ClienteRequest clienteRequest) {
		Cliente cliente = clienteMapper.toEntity(clienteRequest);
		
		CuentaCorriente cuentaCorriente = CuentaCorriente.builder()
	            .saldoPeso(BigDecimal.ZERO)
	            .saldoDolar(BigDecimal.ZERO)
	            .saldoReal(BigDecimal.ZERO)
	            .saldoCrypto(BigDecimal.ZERO)
	            .saldoEuro(BigDecimal.ZERO)
	            .cliente(cliente) // Relacionar con el cliente
	            .build();
		
		 cliente.setCuentaCorriente(cuentaCorriente);
		
		Cliente savedCliente = clienteRepository.save(cliente);
		return clienteMapper.toResponse(savedCliente);	
	}
	
	@Transactional
	public ClienteResponse actualizarCliente(Long id, ClienteRequest clienteRequest) {
		Cliente clienteExistente = clienteRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Cliente con id: " + id + " no encontrado"));
		
		 clienteMapper.updateEntity(clienteExistente, clienteRequest);
	        clienteExistente = clienteRepository.save(clienteExistente);
	        return clienteMapper.toResponse(clienteExistente);		
	}
	
	public void eliminarCliente(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));
        clienteRepository.delete(cliente);
    }

}
