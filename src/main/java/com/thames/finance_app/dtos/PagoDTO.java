package com.thames.finance_app.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.thames.finance_app.enums.TipoEntrega;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagoDTO {
    private Long id;
    private LocalDateTime fecha;
    private TipoEntrega tipoEntrega;
    private BigDecimal valor;
}

