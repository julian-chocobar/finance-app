package com.thames.finance_app.dtos;

import java.math.BigDecimal;
import java.util.List;

import com.thames.finance_app.enums.TipoOperacion;
import com.thames.finance_app.models.Moneda;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class OperacionRequest {

    @NotNull(message = "El tipo de operación es obligatorio.")
    private TipoOperacion tipo;

    @NotNull(message = "El cliente es obligatorio.")
    private String nombreCliente;

    @NotNull(message = "La moneda de origen es obligatoria.")
    private Moneda monedaOrigen;

    @NotNull(message = "El monto de origen es obligatorio.")
    @Positive(message = "El monto de origen debe ser mayor a cero.")
    private BigDecimal montoOrigen;

    @NotNull(message = "La moneda de conversión es obligatoria.")
    private Moneda monedaConversion;
    
    private List<PagoDTO> pagosOrigen;
    
    private List<PagoDTO> pagosConversion; 

    private String nombreReferido;
    
    @PositiveOrZero(message = "El puntaje del referido no puede ser negativo.")
    private BigDecimal puntosReferido;
        
    private Moneda monedaReferido;
    
//  @NotNull(message = "El monto de conversión es obligatorio.")
//  @Positive(message = "El monto de conversión debe ser mayor a cero.")
//  private BigDecimal montoConversion;

//  @NotNull(message = "El tipo de cambio es obligatorio.")
//  @Positive(message = "El tipo de cambio debe ser mayor a cero.")
//  private BigDecimal tipoCambio;

    
	public BigDecimal getTotalPagosOrigen() {
        BigDecimal total = BigDecimal.ZERO;
        for (PagoDTO pago : pagosOrigen) {
            if (pago.getValor() != null) {
                total = total.add(pago.getValor());
            }
        }
        return total;
	}
	
	public BigDecimal getTotalPagosConversion() {
        BigDecimal total = BigDecimal.ZERO;
        for (PagoDTO pago : pagosConversion) {
            if (pago.getValor() != null) {
                total = total.add(pago.getValor());
            }
        }
        return total;
	}

}


