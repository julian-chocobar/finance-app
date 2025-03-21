package com.thames.finance_app.services;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.thames.finance_app.dtos.CajaDTO;
import com.thames.finance_app.enums.TipoOperacion;
import com.thames.finance_app.exceptions.BusinessException;
import com.thames.finance_app.mappers.CajaMapper;
import com.thames.finance_app.models.Caja;
import com.thames.finance_app.models.Moneda;
import com.thames.finance_app.models.MovimientoCaja;
import com.thames.finance_app.models.Operacion;
import com.thames.finance_app.repositories.CajaRepository;
import com.thames.finance_app.repositories.MovimientoCajaRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CajaService {
	
	private final CajaRepository cajaRepository;
    private final MovimientoCajaService movimientoCajaService;
    private final MovimientoCajaRepository movimientoCajaRepository;
    private final CajaMapper cajaMapper;
    

	public List<CajaDTO> obtenerTodas() {		
		List<Caja> cajas = cajaRepository.findAll();
		return cajas.stream()
				.map(cajaMapper::toDTO)
				.collect(Collectors.toList());
	}
	
	public CajaDTO obtenerPorID(Long id) {
		Caja caja = cajaRepository.findById(id)
				.orElseThrow( () -> new EntityNotFoundException("Cliente con id: " + id + " no encontrado"));
		return cajaMapper.toDTO(caja);	
	}
	
	@Transactional
	public CajaDTO crearCaja(CajaDTO dto) {
		verificarNombreUnico(dto.getNombre());
		Caja caja = cajaMapper.toEntity(dto);
		cajaRepository.save(caja);
		return cajaMapper.toDTO(caja);
	}
	
    public boolean existeNombre(String nombre) {
        return cajaRepository.findByNombre(nombre).isPresent();
    }
    
    public void verificarNombreUnico(String nombre) {
        if (existeNombre(nombre)) {
            throw new BusinessException("Nombre ya registrado");
        }
    }
	

	
	public void eliminar(Long id) {
	    Caja caja = cajaRepository.findById(id)
	        .orElseThrow(() -> new EntityNotFoundException("Caja no encontrado"));
        boolean tieneMovimientos = movimientoCajaRepository.existsByCajaId(id);
        if (tieneMovimientos) {
            throw new IllegalStateException("No se puede eliminar la caja porque tiene movimientos registrados.");
        }
        if (caja.getSaldoReal().compareTo(BigDecimal.ZERO) != 0 || caja.getSaldoDisponible().compareTo(BigDecimal.ZERO) !=0) {
            throw new IllegalStateException("No se puede eliminar la caja porque tiene saldo pendiente.");
        }
	   cajaRepository.delete(caja);
	}
		
	
    public void impactoOperacion(Operacion operacion) {
    	Caja cajaOrigen = cajaRepository.findByMoneda(operacion.getMonedaOrigen())
				.orElseThrow(() -> new RuntimeException("Caja no encontrada"));
    	
    	Caja cajaConversion = cajaRepository.findByMoneda(operacion.getMonedaConversion())
				.orElseThrow(() -> new RuntimeException("Caja no encontrada"));
    	
    	if (operacion.getTipo() == TipoOperacion.COMPRA) {
            actualizarSaldoReal(cajaOrigen, operacion.getMontoOrigen(), true); // Suma en caja de origen
            actualizarSaldoDisponible(cajaOrigen, operacion.getTotalPagosOrigen(),true);
            
            actualizarSaldoReal(cajaConversion, operacion.getMontoConversion(), false); // Resta en caja de destino
            actualizarSaldoDisponible(cajaConversion, operacion.getTotalPagosConversion(), false);
    	}
    	
    	if (operacion.getTipo() == TipoOperacion.VENTA) {
            actualizarSaldoReal(cajaOrigen, operacion.getMontoOrigen(), false); // Resta en caja de origen
            actualizarSaldoDisponible(cajaOrigen, operacion.getTotalPagosOrigen(), false);
                       
            actualizarSaldoReal(cajaConversion, operacion.getMontoConversion(), true); // Suma en caja de destino
            actualizarSaldoDisponible(cajaConversion, operacion.getTotalPagosConversion(), true);
    	}
  
        movimientoCajaService.registrarMovimientos(operacion, cajaOrigen, cajaConversion);  
    }
    
   
    public void revertirImpactoOperacion(Operacion operacion) {
        Caja cajaOrigen = cajaRepository.findByMoneda(operacion.getMonedaOrigen())
                .orElseThrow(() -> new RuntimeException("Caja no encontrada"));
        Caja cajaConversion = cajaRepository.findByMoneda(operacion.getMonedaConversion())
                .orElseThrow(() -> new RuntimeException("Caja no encontrada"));
        
        List<MovimientoCaja> movimientos = movimientoCajaRepository.findByOperacion(operacion);
	  
        if (operacion.getTipo() == TipoOperacion.COMPRA) {
            actualizarSaldoReal(cajaOrigen, operacion.getMontoOrigen(), false); // Resta lo sumado en la compra
            actualizarSaldoDisponible(cajaOrigen, operacion.getTotalPagosOrigen(), false);
            
            actualizarSaldoReal(cajaConversion, operacion.getMontoConversion(), true); // Suma lo restado en la compra
            actualizarSaldoDisponible(cajaConversion, operacion.getTotalPagosConversion(), true);
        }
        
        if (operacion.getTipo() == TipoOperacion.VENTA) {
            actualizarSaldoReal(cajaOrigen, operacion.getMontoOrigen(), true); // Suma lo restado en la venta
            actualizarSaldoDisponible(cajaOrigen, operacion.getTotalPagosOrigen(), true);
            
            actualizarSaldoReal(cajaConversion, operacion.getMontoConversion(), false); // Resta lo sumado en la venta
            actualizarSaldoDisponible(cajaConversion, operacion.getTotalPagosConversion(), false);
        }
   
        movimientoCajaRepository.deleteAll(movimientos);
        cajaRepository.save(cajaOrigen);
        cajaRepository.save(cajaConversion);       
    }
        
	@Transactional
	public void actualizarSaldoReal(Caja caja, BigDecimal monto, boolean esIngreso) {
		if (esIngreso) {
			caja.setSaldoReal(caja.getSaldoReal().add(monto));
	    } else {
	    	caja.setSaldoReal(caja.getSaldoReal().subtract(monto));
	    }
	    cajaRepository.save(caja);		
	}
	
	@Transactional
	public void actualizarSaldoDisponible(Caja caja, BigDecimal monto, boolean esIngreso) {
        if (esIngreso) {
            caja.setSaldoDisponible(caja.getSaldoDisponible().add(monto));
        } else {
            caja.setSaldoDisponible(caja.getSaldoDisponible().subtract(monto));
        }

        cajaRepository.save(caja);
	}
	
	public Caja obtenerPorMoneda(Moneda moneda) {
		return cajaRepository.findByMoneda(moneda).orElseThrow(() -> new RuntimeException("Caja no encontrada"));
	}



		
}
