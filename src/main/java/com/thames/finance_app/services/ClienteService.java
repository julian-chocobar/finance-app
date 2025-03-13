package com.thames.finance_app.services;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.thames.finance_app.dtos.TitularRequest;
import com.thames.finance_app.dtos.TitularResponse;
import com.thames.finance_app.exceptions.BusinessException;
import com.thames.finance_app.mappers.TitularMapper;
import com.thames.finance_app.models.Titular;
import com.thames.finance_app.models.CuentaCorriente;
import com.thames.finance_app.models.Moneda;
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
	private final MonedaService monedaService;
	private final TitularMapper clienteMapper;

	public List<TitularResponse> obtenerTodos(){
		List<Titular> clientes = clienteRepository.findAll();
		return clientes.stream()
				.map(clienteMapper::toResponse)
				.collect(Collectors.toList());	
	}
	
	public List<TitularResponse> obtenerTodosClientes(){
		List<Titular> clientes = clienteRepository.findByEsReferidoFalse();
		return clientes.stream()
				.map(clienteMapper::toResponse)
				.collect(Collectors.toList());	
	}
	
	public List<TitularResponse> obtenerTodosReferidos(){
		List<Titular> referidos = clienteRepository.findByEsReferidoTrue();
		return referidos.stream()
				.map(clienteMapper::toResponse)
				.collect(Collectors.toList());	
	}
	
	public TitularResponse obtenerClientePorID(Long id) {
		Titular cliente = clienteRepository.findByIdAndEsReferidoFalse(id)
				.orElseThrow( () -> new EntityNotFoundException("Cliente con id: " + id + " no encontrado"));
		return clienteMapper.toResponse(cliente);	
	}
	
	public Titular obtenerEntidadPorID(Long id) {
		return clienteRepository.findByIdAndEsReferidoFalse(id)
				.orElseThrow( () -> new EntityNotFoundException("Cliente con id: " + id + " no encontrado"));		
	}
	
	public TitularResponse obtenerReferidoPorID(Long id) {
		Titular referidos = clienteRepository.findByIdAndEsReferidoTrue(id)
				.orElseThrow( () -> new EntityNotFoundException("Cliente con id: " + id + " no encontrado"));
		return clienteMapper.toResponse(referidos);
	}
	
	public Titular obtenerClientePorNombre(String nombre) {
		return clienteRepository.findByNombre(nombre)
				.orElseThrow( () -> new EntityNotFoundException("Cliente con nombre: " + nombre + " no encontrado"));
	}
	
	@Transactional
	public TitularResponse crearCliente(TitularRequest clienteRequest) {	
	    verificarNombreUnico(clienteRequest.getNombre());				
	    
	    Titular cliente = clienteMapper.toEntityCliente(clienteRequest);
	    
	    // Obtener todas las monedas desde la base de datos
	    List<Moneda> monedas = monedaService.listarTodas(); 

	    // Inicializar los saldos con BigDecimal.ZERO para cada moneda
	    Map<Moneda, BigDecimal> saldosIniciales = new HashMap<>();
	    for (Moneda moneda : monedas) {
	        saldosIniciales.put(moneda, BigDecimal.ZERO);
	    }

	    CuentaCorriente cuentaCorriente = CuentaCorriente.builder()
	            .titular(cliente)
	            .saldos(saldosIniciales)
	            .build();

	    cliente.setCuentaCorriente(cuentaCorriente);
	    Titular savedCliente = clienteRepository.save(cliente);
	    
	    return clienteMapper.toResponse(savedCliente);
	}


	
	@Transactional
	public TitularResponse crearReferido(TitularRequest clienteRequest) {
		verificarNombreUnico(clienteRequest.getNombre());	
		
		Titular cliente = clienteMapper.toEntityReferido(clienteRequest);
		
		// Obtener todas las monedas desde la base de datos
	    List<Moneda> monedas = monedaService.listarTodas(); 

	    // Inicializar los saldos con BigDecimal.ZERO para cada moneda
	    Map<Moneda, BigDecimal> saldosIniciales = new HashMap<>();
	    for (Moneda moneda : monedas) {
	        saldosIniciales.put(moneda, BigDecimal.ZERO);
	    }

	    CuentaCorriente cuentaCorriente = CuentaCorriente.builder()
	            .titular(cliente)
	            .saldos(saldosIniciales)
	            .build();
		
		 cliente.setCuentaCorriente(cuentaCorriente);
		
		Titular savedCliente = clienteRepository.save(cliente);
		return clienteMapper.toResponse(savedCliente);	
	}
		
	
	@Transactional
	public TitularResponse actualizarCliente(Long id, TitularRequest clienteRequest) {
	    Titular cliente = clienteRepository.findById(id)
	        .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));

	    cliente.setNombre(clienteRequest.getNombre());
	    cliente.setTelefono(clienteRequest.getTelefono());
	    clienteRepository.save(cliente);

	    return clienteMapper.toResponse(cliente);
	}
	
	public void eliminarCliente(Long id) {
	    Titular cliente = clienteRepository.findById(id)
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
