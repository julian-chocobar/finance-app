package com.thames.finance_app.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.thames.finance_app.enums.TipoMovimiento;

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
@Table(name = "movimientos_caja")
@Setter
@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoCaja {
	@Id
	@GeneratedValue(
			strategy = GenerationType.SEQUENCE,
			generator = "movimientos_caja_generator")
	@SequenceGenerator (
			name = "movimientos_caja_generator",
			sequenceName = "movimientos_caja_sequence",
			allocationSize = 1)
	private Long id;
	
	private LocalDateTime fecha;
	
	@Enumerated(EnumType.STRING)
	private TipoMovimiento tipoMovimiento;
	
	@ManyToOne
    @JoinColumn(name = "caja_id", nullable = true)
    private Caja caja;
	
	@ManyToOne
    @JoinColumn(name = "operacion_id", nullable = true)
    private Operacion operacion;
	
	private BigDecimal monto;
	
	private BigDecimal montoEjecutado;

}
