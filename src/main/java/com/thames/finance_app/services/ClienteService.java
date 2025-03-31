package com.thames.finance_app.services;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.thames.finance_app.dtos.TitularRequest;
import com.thames.finance_app.dtos.TitularResponse;
import com.thames.finance_app.enums.TipoTitular;
import com.thames.finance_app.exceptions.BusinessException;
import com.thames.finance_app.mappers.TitularMapper;
import com.thames.finance_app.models.CuentaCorriente;
import com.thames.finance_app.models.Moneda;
import com.thames.finance_app.models.Titular;
import com.thames.finance_app.repositories.OperacionRepository;
import com.thames.finance_app.repositories.TitularRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClienteService {

	private final TitularRepository titularRepository;
	private final OperacionRepository operacionRepository;
	private final MonedaService monedaService;
	private final TitularMapper titularMapper;

	public Page<TitularResponse> obtenerTodos(Pageable pageable){
		return  titularRepository.findByTipo(TipoTitular.CLIENTE, pageable)
				.map(titularMapper::toResponse);
	}

	public TitularResponse obtenerPorID(Long id) {
		Titular cliente = titularRepository.findById(id)
				.orElseThrow( () -> new EntityNotFoundException("Cliente con id: " + id + " no encontrado"));
		return titularMapper.toResponse(cliente);
	}


	public Titular obtenerPorNombre(String nombre) {
		return titularRepository.findByNombreAndTipo(nombre, TipoTitular.CLIENTE )
				.orElseThrow( () -> new EntityNotFoundException("Cliente con nombre: " + nombre + " no encontrado"));
	}

	public Page<TitularResponse> buscarPorNombre(String nombre, Pageable pageable) {
        return titularRepository.findByNombreContainingIgnoreCaseAndTipo(nombre, TipoTitular.CLIENTE, pageable)
                .map(titular -> TitularResponse.builder()
                    .id(titular.getId())
                    .nombre(titular.getNombre())
                    .telefono(titular.getTelefono())
                    .email(titular.getEmail())
                    .direccion(titular.getDireccion()).build()
                );
    }

	@Transactional
	public TitularResponse crear(TitularRequest clienteRequest) {
	    verificarNombreUnico(clienteRequest.getNombre());

	    Titular cliente = titularMapper.toEntityCliente(clienteRequest);

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
	    Titular savedCliente = titularRepository.save(cliente);

	    return titularMapper.toResponse(savedCliente);
	}

	@Transactional
	public TitularResponse actualizar(Long id, TitularRequest clienteRequest) {
	    Titular cliente = titularRepository.findById(id)
	        .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));

	    cliente.setNombre(clienteRequest.getNombre());
	    cliente.setTelefono(clienteRequest.getTelefono());
	    cliente.setEmail(clienteRequest.getEmail());
	    cliente.setDireccion(clienteRequest.getDireccion());
	    titularRepository.save(cliente);

	    return titularMapper.toResponse(cliente);
	}

	public void eliminar(Long id) {
	    Titular cliente = titularRepository.findById(id)
	        .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));

	   if (operacionRepository.existsByCuentaCorrienteId(cliente.getCuentaCorriente().getId())){
		   throw new BusinessException("No se puede eliminar un cliente con operaciones registradas.");
	   }
	   titularRepository.delete(cliente);
	}

    public boolean existeNombre(String nombre) {
        return titularRepository.findByNombre(nombre).isPresent();
    }

    public void verificarNombreUnico(String nombre) {
        if (existeNombre(nombre)) {
            throw new BusinessException("Nombre ya registrado");
        }
    }

	

}
