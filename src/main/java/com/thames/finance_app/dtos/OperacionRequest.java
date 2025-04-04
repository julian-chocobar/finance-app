package com.thames.finance_app.dtos;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.thames.finance_app.enums.TipoOperacion;
import com.thames.finance_app.validations.ValidReferido;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
@ValidReferido
public class OperacionRequest {

    @NotNull(message = "El tipo de operación es obligatorio.")
    private TipoOperacion tipo;

    @NotNull(message = "El cliente es obligatorio.")
    private String nombreCliente;

    @NotNull(message = "La moneda de origen es obligatoria.")
    private String monedaOrigen;

    @NotNull(message = "El monto de origen es obligatorio.")
    @Positive(message = "El monto de origen debe ser mayor a cero.")
    private BigDecimal montoOrigen;

    @NotNull(message = "El tipo de cambio es obligatorio.")
    @Positive(message = "El monto de origen debe ser mayor a cero.")
    private BigDecimal valorTipoCambio;

    @NotNull(message = "La moneda de conversión es obligatoria.")
    private String monedaConversion;

    @Builder.Default
    private List<PagoDTO> pagosOrigen = new ArrayList<>();

    @Builder.Default
    private List<PagoDTO> pagosConversion = new ArrayList<>();

    private String nombreReferido;

    @PositiveOrZero(message = "El puntaje del referido no puede ser negativo.")
    private BigDecimal puntosReferido;

    private String monedaReferido;

    @Positive(message = "La ganancia debe ser mayor a cero.")
    private BigDecimal gananciaReferido;

    // Constructor para asegurar inicialización
    public OperacionRequest() {
        if (this.pagosOrigen == null) {
			this.pagosOrigen = new ArrayList<>();
		}
        if (this.pagosConversion == null) {
			this.pagosConversion = new ArrayList<>();
		}
    }

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


