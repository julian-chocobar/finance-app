package com.thames.finance_app.services;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.thames.finance_app.dtos.MonedaDTO;
import com.thames.finance_app.exceptions.BusinessException;
import com.thames.finance_app.mappers.MonedaMapper;
import com.thames.finance_app.models.CuentaCorriente;
import com.thames.finance_app.models.Moneda;
import com.thames.finance_app.repositories.CtaCteRepository;
import com.thames.finance_app.repositories.MonedaRepository;
import com.thames.finance_app.repositories.OperacionRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MonedaService {

	private final MonedaRepository monedaRepository;
	private final OperacionRepository operacionRepository;
	private final CtaCteRepository ctaCteRepository;

	private final MonedaMapper monedaMapper;

	public Moneda buscarPorNombre(String nombre) {
		return  monedaRepository.findByNombre(nombre)
				.orElseThrow( () -> new EntityNotFoundException("Moneda con nombre: " + nombre + " no encontrada"));
	}

	public List<Moneda> listarTodas() {
		return monedaRepository.findAll();
	}

    public MonedaDTO crearMoneda(MonedaDTO monedaDTO) {
    	verificarNombreUnico(monedaDTO.getNombre());
        Moneda moneda = monedaMapper.toEntity(monedaDTO);
        moneda = monedaRepository.save(moneda);
        return monedaMapper.toDTO(moneda);
    }



    public MonedaDTO obtenerMonedaPorId(Long id) {
        Moneda moneda = monedaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Moneda no encontrada con ID: " + id));
        return monedaMapper.toDTO(moneda);
    }

    public List<MonedaDTO> listarTodasLasMonedas() {
        List<Moneda> monedas = monedaRepository.findAll();
        return monedas.stream()
                .map(monedaMapper::toDTO)
                .collect(Collectors.toList());
    }

    public MonedaDTO actualizarMoneda(Long id, MonedaDTO monedaDTO) {
        Moneda moneda = monedaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Moneda no encontrada con ID: " + id));
        monedaMapper.updateEntity(moneda, monedaDTO);
        moneda = monedaRepository.save(moneda);
        return monedaMapper.toDTO(moneda);
    }

    public void eliminarMoneda(Long id) {
        // Verificar si la moneda existe
        Moneda moneda = monedaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Moneda no encontrada con ID: " + id));

        // Verificar si la moneda está asociada a operaciones
        if (operacionRepository.existsByMonedaOrigen(moneda) || operacionRepository.existsByMonedaConversion(moneda)) {
            throw new RuntimeException("No se puede eliminar la moneda porque está asociada a operaciones.");
        }

        // Verificar si la moneda tiene saldo distinto de cero en cuentas corrientes
        List<CuentaCorriente> cuentas = ctaCteRepository.findAll();
        for (CuentaCorriente cuenta : cuentas) {
            BigDecimal saldo = cuenta.getSaldos().get(moneda);
            if (saldo != null && saldo.compareTo(BigDecimal.ZERO) != 0) {
                throw new RuntimeException("No se puede eliminar la moneda porque tiene saldo distinto de cero en cuentas corrientes.");
            }
        }
        // Si pasa las verificaciones, eliminar la moneda
        monedaRepository.deleteById(id);
    }

    public boolean existeNombre(String nombre) {
        return monedaRepository.findByNombre(nombre).isPresent();
    }

    public void verificarNombreUnico(String nombre) {
        if (existeNombre(nombre)) {
            throw new BusinessException("Nombre ya registrado");
        }
    }

	public Moneda buscarPorCodigo(String codigo) {
		return  monedaRepository.findByCodigo(codigo)
				.orElseThrow( () -> new EntityNotFoundException("Moneda con nombre: " + codigo + " no encontrada"));
	}



}
