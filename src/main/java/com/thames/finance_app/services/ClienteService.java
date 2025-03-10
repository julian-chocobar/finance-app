package com.thames.finance_app.services;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.thames.finance_app.dtos.ClienteRequest;
import com.thames.finance_app.dtos.ClienteResponse;
import com.thames.finance_app.enums.Moneda;
import com.thames.finance_app.exceptions.BusinessException;
import com.thames.finance_app.mappers.ClienteMapper;
import com.thames.finance_app.models.Cliente;
import com.thames.finance_app.models.CuentaCorriente;
import com.thames.finance_app.repositories.ClienteRepository;
import com.thames.finance_app.repositories.OperacionRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClienteService {
	
	private final ClienteRepository clienteRepository;	
	private final OperacionRepository operacionRepository;
	private final ClienteMapper clienteMapper;

	public List<ClienteResponse> obtenerTodos(){
		List<Cliente> clientes = clienteRepository.findAll();
		return clientes.stream()
				.map(clienteMapper::toResponse)
				.collect(Collectors.toList());	
	}
	
	public List<ClienteResponse> obtenerTodosClientes(){
		List<Cliente> clientes = clienteRepository.findByEsReferidoFalse();
		return clientes.stream()
				.map(clienteMapper::toResponse)
				.collect(Collectors.toList());	
	}
	
	public List<ClienteResponse> obtenerTodosReferidos(){
		List<Cliente> referidos = clienteRepository.findByEsReferidoTrue();
		return referidos.stream()
				.map(clienteMapper::toResponse)
				.collect(Collectors.toList());	
	}
	
	public ClienteResponse obtenerClientePorID(Long id) {
		Cliente cliente = clienteRepository.findByIdAndEsReferidoFalse(id)
				.orElseThrow( () -> new EntityNotFoundException("Cliente con id: " + id + " no encontrado"));
		return clienteMapper.toResponse(cliente);	
	}
	
	public Cliente obtenerEntidadPorID(Long id) {
		return clienteRepository.findByIdAndEsReferidoFalse(id)
				.orElseThrow( () -> new EntityNotFoundException("Cliente con id: " + id + " no encontrado"));		
	}
	
	public ClienteResponse obtenerReferidoPorID(Long id) {
		Cliente referidos = clienteRepository.findByIdAndEsReferidoTrue(id)
				.orElseThrow( () -> new EntityNotFoundException("Cliente con id: " + id + " no encontrado"));
		return clienteMapper.toResponse(referidos);
	}
	
	public Cliente obtenerClientePorNombre(String nombre) {
		return clienteRepository.findByNombre(nombre)
				.orElseThrow( () -> new EntityNotFoundException("Cliente con nombre: " + nombre + " no encontrado"));
	}
	
	@Transactional
	public ClienteResponse crearCliente(ClienteRequest clienteRequest) {	
		verificarNombreUnico(clienteRequest.getNombre());				
	    Cliente cliente = clienteMapper.toEntity(clienteRequest);
	    // Inicializar los saldos con BigDecimal.ZERO para todas las monedas
	    Map<Moneda, BigDecimal> saldosIniciales = new HashMap<>();
	    for (Moneda moneda : Moneda.values()) {
	        saldosIniciales.put(moneda, BigDecimal.ZERO);
	    }
	    CuentaCorriente cuentaCorriente = CuentaCorriente.builder()
	            .cliente(cliente)
	            .saldos(saldosIniciales)
	            .build();

	    cliente.setCuentaCorriente(cuentaCorriente);
	    Cliente savedCliente = clienteRepository.save(cliente);
	    return clienteMapper.toResponse(savedCliente);
	}

	
	@Transactional
	public ClienteResponse crearReferido(ClienteRequest clienteRequest) {
		Cliente cliente = clienteMapper.toEntityReferido(clienteRequest);
		
	    Map<Moneda, BigDecimal> saldosIniciales = new HashMap<>();
	    for (Moneda moneda : Moneda.values()) {
	        saldosIniciales.put(moneda, BigDecimal.ZERO);
	    }

	    CuentaCorriente cuentaCorriente = CuentaCorriente.builder()
	            .cliente(cliente)
	            .saldos(saldosIniciales)
	            .build();
		
		 cliente.setCuentaCorriente(cuentaCorriente);
		
		Cliente savedCliente = clienteRepository.save(cliente);
		return clienteMapper.toResponse(savedCliente);	
	}
		
	
	@Transactional
	public ClienteResponse actualizarCliente(Long id, ClienteRequest clienteRequest) {
	    Cliente cliente = clienteRepository.findById(id)
	        .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));

	    cliente.setNombre(clienteRequest.getNombre());
	    cliente.setTelefono(clienteRequest.getTelefono());
	    clienteRepository.save(cliente);

	    return clienteMapper.toResponse(cliente);
	}
	
	public void eliminarCliente(Long id) {
	    Cliente cliente = clienteRepository.findById(id)
	        .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));

	   if (operacionRepository.existsByCuentaCorrienteId(cliente.getCuentaCorriente().getId())){
		   throw new BusinessException("No se puede eliminar un cliente con operaciones registradas.");
	   }
	    

	    clienteRepository.delete(cliente);
	}
	
	
    public boolean existeNombre(String nombre) {
        return clienteRepository.findByNombre(nombre).isPresent();
    }
    
    public void verificarNombreUnico(String nombre) {
        if (existeNombre(nombre)) {
            throw new BusinessException("Nombre ya registrado");
        }
    }

}
