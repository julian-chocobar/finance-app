package com.thames.finance_app.mappers;

import org.springframework.stereotype.Component;

import com.thames.finance_app.dtos.MonedaDTO;
import com.thames.finance_app.dtos.TipoCambioDTO;
import com.thames.finance_app.models.Moneda;
import com.thames.finance_app.models.TipoCambio;
import com.thames.finance_app.repositories.MonedaRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MonedaMapper {

    private final MonedaRepository monedaRepository;

    public Moneda toEntity(MonedaDTO request) {
        return Moneda.builder()
                .nombre(request.getNombre())
                .codigo(request.getCodigo())
                .build();
    }

    public MonedaDTO toDTO(Moneda moneda) {
        return MonedaDTO.builder()
                .codigo(moneda.getCodigo())
                .nombre(moneda.getNombre())
                .build();
    }

    public void updateEntity(Moneda moneda, MonedaDTO request) {
        moneda.setNombre(request.getNombre());
        moneda.setCodigo(request.getCodigo());
    }

    public TipoCambioDTO toDTO(TipoCambio moneda) {
        return TipoCambioDTO.builder()
                .id(moneda.getId())
                .codigoMonedaOrigen(moneda.getMonedaOrigen().getNombre())
                .codigoMonedaConversion(moneda.getMonedaConversion().getNombre())
                .valorCompra(moneda.getValorCompra())
                .valorVenta(moneda.getValorVenta())
                .build();
    }

    public TipoCambio toEntity(TipoCambioDTO request) {
        Moneda monedaOrigen = monedaRepository.findByCodigo(request.getCodigoMonedaOrigen())
        .orElseThrow(() -> new RuntimeException("Moneda no encontrada con código: " + request.getCodigoMonedaOrigen()));
        Moneda monedaDestino = monedaRepository.findByCodigo(request.getCodigoMonedaConversion())
        .orElseThrow(() -> new RuntimeException("Moneda no encontrada con código: " + request.getCodigoMonedaConversion()));
        
        return TipoCambio.builder()
                .monedaOrigen(monedaOrigen)
                .monedaConversion(monedaDestino)
                .valorCompra(request.getValorCompra())
                .valorVenta(request.getValorVenta())
                .build();
    }

    public void updateEntity(TipoCambio moneda, TipoCambioDTO request) {
        Moneda monedaOrigen = monedaRepository.findByCodigo(request.getCodigoMonedaOrigen())
        .orElseThrow(() -> new RuntimeException("Moneda no encontrada con código: " + request.getCodigoMonedaOrigen()));
        Moneda monedaConversion = monedaRepository.findByCodigo(request.getCodigoMonedaConversion())
        .orElseThrow(() -> new RuntimeException("Moneda no encontrada con código: " + request.getCodigoMonedaConversion()));

        moneda.setMonedaOrigen(monedaOrigen);
        moneda.setMonedaConversion(monedaConversion);
        moneda.setValorCompra(request.getValorCompra());
        moneda.setValorVenta(request.getValorVenta());
    }
}