package com.thames.finance_app.services;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.thames.finance_app.dtos.CajaDTO;
import com.thames.finance_app.enums.TipoOperacion;
import com.thames.finance_app.mappers.CajaMapper;
import com.thames.finance_app.models.Caja;
import com.thames.finance_app.models.Moneda;
import com.thames.finance_app.models.Operacion;
import com.thames.finance_app.repositories.CajaRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CajaService {
	
	private final CajaRepository cajaRepository;
    private final MovimientoCajaService movimientoCajaService;
    private final CajaMapper cajaMapper;
    

	public List<CajaDTO> obtenerCajas() {		
		List<Caja> cajas = cajaRepository.findAll();
		return cajas.stream()
				.map(cajaMapper::toDTO)
				.collect(Collectors.toList());
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
