package com.thames.finance_app.services;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.thames.finance_app.models.Moneda;
import com.thames.finance_app.models.TipoCambio;
import com.thames.finance_app.repositories.TipoCambioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TipoCambioService {


    private final TipoCambioRepository tipoCambioRepository;

    public BigDecimal convertirMoneda(Moneda monedaOrigen, Moneda monedaDestino, BigDecimal monto, boolean esCompra) {
        // Busca el tipo de cambio en la base de datos
        TipoCambio tipoCambio = tipoCambioRepository.findByMonedaOrigenAndMonedaDestino(monedaOrigen, monedaDestino)
                .orElseThrow(() -> new RuntimeException(
                        "Tipo de cambio no encontrado para " + monedaOrigen.getNombre() + " a " + monedaDestino.getNombre()));

        // Obtiene la tasa de cambio según la operación (compra o venta)
        BigDecimal tasaCambio = esCompra ? tipoCambio.getValorCompra() : tipoCambio.getValorVenta();

        // Realiza la conversión del monto
        return monto.multiply(tasaCambio);
    }



    public BigDecimal obtenerTipoCambio(Moneda monedaOrigen, Moneda monedaDestino, boolean esCompra) {
        // Busca el tipo de cambio en la base de datos
        TipoCambio tipoCambio = tipoCambioRepository.findByMonedaOrigenAndMonedaDestino(monedaOrigen, monedaDestino)
                .orElseThrow(() -> new RuntimeException(
                        "Tipo de cambio no encontrado para " + monedaOrigen.getNombre() + " a " + monedaDestino.getNombre()));

        // Devuelve el valor de compra o venta según la operación
        return esCompra ? tipoCambio.getValorCompra() : tipoCambio.getValorVenta();
    }


}
