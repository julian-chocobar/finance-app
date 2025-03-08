package com.thames.finance_app.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PagoResponse {
    private Long id;
    private LocalDateTime fecha;
    private String tipoEntrega;
    private BigDecimal valor;
}

