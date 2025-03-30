package com.thames.finance_app.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TitularResponse {
	private Long id;
    private String nombre;
    private String telefono;
    private String email;
    private String direccion;

    // Información básica de la cuenta corriente
//    private Long cuentaCorrienteId;
//    private Map<Moneda, BigDecimal> saldos;

}
