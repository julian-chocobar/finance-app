package com.thames.finance_app.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.thames.finance_app.enums.EstadoOperacion;
import com.thames.finance_app.enums.Moneda;
import com.thames.finance_app.enums.TipoEntrega;
import com.thames.finance_app.enums.TipoOperacion;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
    @JoinColumn(name = "cuenta_corriente_id", nullable = true)
    private CuentaCorriente cuentaCorriente; 
	
	@Enumerated(EnumType.STRING)
	private Moneda monedaOrigen;
	
	private BigDecimal montoOrigen;
	
	private BigDecimal montoOrigenEjecutado;
	
	@Enumerated(EnumType.STRING)
	private Moneda monedaConversion;
	
	private BigDecimal montoConversion;
	
	private BigDecimal montoConversionEjecutado;
	
	private BigDecimal tipoCambio;
	
	@Enumerated(EnumType.STRING)
	private EstadoOperacion estado;
	
	private BigDecimal saldoResultante;
	
	@Enumerated(EnumType.STRING)
	private TipoEntrega tipoEntrega;
	
	@ManyToOne
	@JoinColumn(name = "cuenta_corriente_referido_id", nullable = true) // Puede ser null si no hay referido
	private CuentaCorriente cuentaCorrienteReferido; 
	
	@ManyToOne
	private Liquidador liquidador;

	private BigDecimal montoLiquidador;
	
}
