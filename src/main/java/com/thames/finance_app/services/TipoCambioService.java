package com.thames.finance_app.services;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.thames.finance_app.dtos.TipoCambioDTO;
import com.thames.finance_app.models.Moneda;
import com.thames.finance_app.models.TipoCambio;
import com.thames.finance_app.repositories.TipoCambioRepository;
import com.thames.finance_app.mappers.MonedaMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TipoCambioService {


    private final TipoCambioRepository tipoCambioRepository;
    private final MonedaMapper monedaMapper;
    
    public Page<TipoCambioDTO> listar(Pageable pageable) {
        return tipoCambioRepository.findAll(pageable).map(monedaMapper::toDTO);
    }

    public BigDecimal obtenerTipoCambio(Moneda monedaOrigen, Moneda monedaDestino, boolean esCompra) {
        // Busca el tipo de cambio en la base de datos
        TipoCambio tipoCambio = tipoCambioRepository.findByMonedaOrigenAndMonedaConversion(monedaOrigen, monedaDestino)
                .orElseThrow(() -> new RuntimeException(
                        "Tipo de cambio no encontrado para " + monedaOrigen.getNombre() + " a " + monedaDestino.getNombre()));

        // Devuelve el valor de compra o venta según la operación
        return esCompra ? tipoCambio.getValorCompra() : tipoCambio.getValorVenta();
    }

    public TipoCambioDTO obtenerDTOPorId(Long id) {
        TipoCambio tipoCambio = tipoCambioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tipo de cambio no encontrado con ID: " + id));
        return monedaMapper.toDTO(tipoCambio);
    }
    
    public TipoCambioDTO crearTipoCambio(TipoCambioDTO dto) {
    	TipoCambio tipoCambio = monedaMapper.toEntity(dto);
    	tipoCambio = tipoCambioRepository.save(tipoCambio);
    	return monedaMapper.toDTO(tipoCambio);
    }

    public TipoCambioDTO actualizarTipoCambio(TipoCambioDTO dto, Long id) {
        TipoCambio tipoCambio = tipoCambioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tipo de cambio no encontrado con ID: " + id));
        monedaMapper.updateEntity(tipoCambio, dto);
        tipoCambio = tipoCambioRepository.save(tipoCambio);
        return monedaMapper.toDTO(tipoCambio);
    }

    public void eliminarTipoCambio(Long id) {
        TipoCambio tipoCambio = tipoCambioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tipo de cambio no encontrado con ID: " + id));
        tipoCambioRepository.delete(tipoCambio);
    }

    public BigDecimal convertirMoneda(Moneda monedaOrigen, Moneda monedaDestino, BigDecimal monto, boolean esCompra) {
        // Busca el tipo de cambio en la base de datos
        TipoCambio tipoCambio = tipoCambioRepository.findByMonedaOrigenAndMonedaConversion(monedaOrigen, monedaDestino)
                .orElseThrow(() -> new RuntimeException(
                        "Tipo de cambio no encontrado para " + monedaOrigen.getNombre() + " a " + monedaDestino.getNombre()));

        // Obtiene la tasa de cambio según la operación (compra o venta)
        BigDecimal tasaCambio = esCompra ? tipoCambio.getValorCompra() : tipoCambio.getValorVenta();

        // Realiza la conversión del monto
        return monto.multiply(tasaCambio);
    }

}
