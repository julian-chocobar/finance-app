package com.thames.finance_app.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

import com.thames.finance_app.enums.TipoOperacion;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "operaciones")
@Setter
@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Operacion {
	
	@Id
	@GeneratedValue(
			strategy = GenerationType.SEQUENCE,
			generator = "operacion_generator")
	@SequenceGenerator (
			name = "operacion_generator",
			sequenceName = "operacion_sequence",
			allocationSize = 1)
	private Long id;
	
	private LocalDateTime fecha;
	
	@Enumerated(EnumType.STRING)
	private TipoOperacion tipo;
	
	@ManyToOne
    @JoinColumn(name = "cuenta_corriente_id", nullable = false)
    private CuentaCorriente cuentaCorriente; 
	
	@ManyToOne
	@JoinColumn(name = "moneda_origen_id", nullable = false)
	private Moneda monedaOrigen; // USD, PESO, EURO, REAL, CRYPTO	
	
	private BigDecimal montoOrigen;	
	
	private BigDecimal valorTipoCambio;
	
	@ManyToOne
	@JoinColumn(name = "moneda_conversion_id", nullable = false)
	private Moneda monedaConversion;
	
	private BigDecimal montoConversion;
  
	@Builder.Default
	@OneToMany(mappedBy = "operacion", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Pago> pagosOrigen = new ArrayList<>();
	
	@Builder.Default
	@OneToMany(mappedBy = "operacion", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Pago> pagosConversion = new ArrayList<>();
			
	@ManyToOne
	@JoinColumn(name = "cuenta_corriente_referido_id", nullable = true) 
	private CuentaCorriente cuentaCorrienteReferido; 
	
	private BigDecimal puntosReferido;
	
	@ManyToOne
	@JoinColumn(name = "moneda_referido_id", nullable = true)
	private Moneda monedaReferido; 
	
//	private BigDecimal gananciaReferido;
	
	public BigDecimal getTotalPagosOrigen() {
        BigDecimal total = BigDecimal.ZERO;
        for (Pago pago : pagosOrigen) {
            if (pago.getValor() != null) {
                total = total.add(pago.getValor());
            }
        }
        return total;
	}
	
	public BigDecimal getTotalPagosConversion() {
        BigDecimal total = BigDecimal.ZERO;
        for (Pago pago : pagosConversion) {
            if (pago.getValor() != null) {
                total = total.add(pago.getValor());
            }
        }
        return total;
	}
		
	public boolean estaCompleta() {
		if ( getTotalPagosOrigen().compareTo(montoOrigen) == 0 
				&& getTotalPagosConversion().compareTo(getMontoConversion()) == 0){
			return true;
		}
		return false;
	}
	
//	public BigDecimal getMontoConversion() {
//		return this.montoOrigen.multiply(this.valorTipoCambio);		
//	}
	
	public BigDecimal getGananciaReferido(BigDecimal monto) {
		return monto.multiply(puntosReferido);
	}
	
		
}
