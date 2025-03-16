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
public class ReferidoService {

	private final TitularRepository titularRepository;
	private final OperacionRepository operacionRepository;
	private final MonedaService monedaService;
	private final TitularMapper titularMapper;
	
	public Page<TitularResponse> obtenerTodosReferidos(Pageable pageable){
		return  titularRepository.findByTipo(TipoTitular.REFERIDO, pageable)
				.map(titularMapper::toResponse);
	}
	
	public TitularResponse obtenerReferidoPorID(Long id) {
		Titular referidos = titularRepository.findById(id)
				.orElseThrow( () -> new EntityNotFoundException("Cliente con id: " + id + " no encontrado"));
		return titularMapper.toResponse(referidos);
	}
	
	@Transactional
	public TitularResponse crearReferido(TitularRequest clienteRequest) {
		verificarNombreUnico(clienteRequest.getNombre());	
		
		Titular cliente = titularMapper.toEntityReferido(clienteRequest);
		
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
	
	public void eliminar(Long id) {
	    Titular referido = titularRepository.findById(id)
	        .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));

	   if (operacionRepository.existsByCuentaCorrienteId(referido.getCuentaCorriente().getId())){
		   throw new BusinessException("No se puede eliminar un referido con operaciones registradas.");
	   }
	    
	   titularRepository.delete(referido);
	}
	
    public boolean existeNombre(String nombre) {
        return titularRepository.findByNombre(nombre).isPresent();
    }
    
    public void verificarNombreUnico(String nombre) {
        if (existeNombre(nombre)) {
            throw new BusinessException("Nombre ya registrado");
        }
    }

    @Transactional
	public TitularResponse actualizar(Long id, TitularRequest referidoRequest) {
	    Titular referido = titularRepository.findById(id)
		        .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));

	    referido.setNombre(referidoRequest.getNombre());
	    referido.setTelefono(referidoRequest.getTelefono());
		titularRepository.save(referido);

		    return titularMapper.toResponse(referido);
	}

	public Titular obtenerPorNombre(String nombre) {
		return titularRepository.findByNombreAndTipo(nombre, TipoTitular.REFERIDO )
				.orElseThrow( () -> new EntityNotFoundException("Cliente con nombre: " + nombre + " no encontrado"));
	}
	
}
