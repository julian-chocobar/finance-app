package com.thames.finance_app.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.thames.finance_app.enums.TipoEntrega;

import lombok.Data;

@Data
public class PagoRequest {
    private LocalDateTime fecha;
    private TipoEntrega tipoEntrega; 
    private BigDecimal valor;
}

