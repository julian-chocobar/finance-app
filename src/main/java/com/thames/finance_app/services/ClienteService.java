package com.thames.finance_app.services;

import java.math.BigDecimal;
import java.util.List;
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
import com.thames.finance_app.repositories.CtaCteRepository;
import com.thames.finance_app.repositories.OperacionRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClienteService {
	
	private final ClienteRepository clienteRepository;
	private final CtaCteRepository ctaCteRepository;
	private final CtaCteService ctaCteService;
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
	
	public ClienteResponse obtenerReferidoPorID(Long id) {
		Cliente referidos = clienteRepository.findByIdAndEsReferidoTrue(id)
				.orElseThrow( () -> new EntityNotFoundException("Cliente con id: " + id + " no encontrado"));
		return clienteMapper.toResponse(referidos);
	}
	
	@Transactional
	public ClienteResponse crearCliente(ClienteRequest clienteRequest) {
		Cliente cliente = clienteMapper.toEntity(clienteRequest);
		
		CuentaCorriente cuentaCorriente = CuentaCorriente.builder()
	            .saldoPesos(BigDecimal.ZERO)
	            .saldoDolares(BigDecimal.ZERO)
	            .saldoReales(BigDecimal.ZERO)
	            .saldoCrypto(BigDecimal.ZERO)
	            .saldoEuros(BigDecimal.ZERO)
	            .cliente(cliente) 
	            .build();
		
		 cliente.setCuentaCorriente(cuentaCorriente);
		
		Cliente savedCliente = clienteRepository.save(cliente);
		return clienteMapper.toResponse(savedCliente);	
	}
	
	@Transactional
	public ClienteResponse crearReferido(ClienteRequest clienteRequest) {
		Cliente cliente = clienteMapper.toEntityReferido(clienteRequest);
		
		CuentaCorriente cuentaCorriente = CuentaCorriente.builder()
	            .saldoPesos(BigDecimal.ZERO)
	            .saldoDolares(BigDecimal.ZERO)
	            .saldoReales(BigDecimal.ZERO)
	            .saldoCrypto(BigDecimal.ZERO)
	            .saldoEuros(BigDecimal.ZERO)
	            .cliente(cliente) 
	            .build();
		
		 cliente.setCuentaCorriente(cuentaCorriente);
		
		Cliente savedCliente = clienteRepository.save(cliente);
		return clienteMapper.toResponse(savedCliente);	
	}
	
	public BigDecimal obtenerSaldoReferido(Long referidoId, Moneda moneda) {
	    CuentaCorriente cuenta = ctaCteRepository.findByClienteId(referidoId)
	        .orElseThrow(() -> new BusinessException("Cuenta corriente del referido no encontrada"));
	    
	    return ctaCteService.obtenerSaldoPorMoneda(cuenta, moneda);
	}
	
	
	@Transactional
	public ClienteResponse actualizarCliente(Long id, ClienteRequest clienteRequest) {
	    Cliente cliente = clienteRepository.findById(id)
	        .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));

	    if (cliente.isEsReferido() != clienteRequest.isEsReferido()) {
	        throw new BusinessException("No se puede cambiar el estado de referenciado de un cliente.");
	    }

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

}
