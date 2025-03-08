package com.thames.finance_app.dtos;

import java.math.BigDecimal;
import java.util.List;

import com.thames.finance_app.enums.EstadoOperacion;
import com.thames.finance_app.enums.Moneda;
import com.thames.finance_app.enums.TipoOperacion;

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

    @NotNull(message = "El monto de conversión es obligatorio.")
    @Positive(message = "El monto de conversión debe ser mayor a cero.")
    private BigDecimal montoConversion;

    @NotNull(message = "El tipo de cambio es obligatorio.")
    @Positive(message = "El tipo de cambio debe ser mayor a cero.")
    private BigDecimal tipoCambio;
    
    private List<PagoRequest> pagosOrigen;
    
    private List<PagoRequest> pagosConversion; 

    private String nombreReferido;
    
    @PositiveOrZero(message = "El puntaje del referido no puede ser negativo.")
    private BigDecimal puntosReferido;
    
    private BigDecimal gananciaReferido;
    
    @NotNull(message = "La moneda del referido es obligatoria si hay ganancia.")
    private Moneda monedaReferido;

    
    
    
    
//    private Long liquidadorId;
//
//    @PositiveOrZero(message = "El monto del liquidador no puede ser negativo.")
//    private BigDecimal montoLiquidador;
 
//  @NotNull(message = "El estado es obligatorio.")
//  private EstadoOperacion estado;
   
	public BigDecimal getTotalPagosOrigen() {
        BigDecimal total = BigDecimal.ZERO;
        for (PagoRequest pago : pagosOrigen) {
            if (pago.getValor() != null) {
                total = total.add(pago.getValor());
            }
        }
        return total;
	}
	
	public BigDecimal getTotalPagosConversion() {
        BigDecimal total = BigDecimal.ZERO;
        for (PagoRequest pago : pagosConversion) {
            if (pago.getValor() != null) {
                total = total.add(pago.getValor());
            }
        }
        return total;
	}
	
	public EstadoOperacion getEstado() {
    	if (estaCompleta()) {
    		return EstadoOperacion.COMPLETA;
        } else {
       	 	return EstadoOperacion.PARCIAL;
        }
	}
	
	public boolean estaCompleta() {
		if ( getTotalPagosOrigen().compareTo(montoOrigen) == 0 
				&& getTotalPagosConversion().compareTo(montoConversion) == 0){
			return true;
		}
		return false;
	}

}


