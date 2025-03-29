package com.thames.finance_app.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.thames.finance_app.enums.TipoEntrega;
import com.thames.finance_app.enums.TipoPago;

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
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pagos_operacion")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pago {

	@Id
	@GeneratedValue(
			strategy = GenerationType.SEQUENCE,
			generator = "pagos_operacion_generator")
	@SequenceGenerator (
			name = "pagos_operacion_generator",
			sequenceName = "pagos_operacion_sequence",
			allocationSize = 1)
	private Long id;

	private LocalDateTime fecha;

	@Enumerated(EnumType.STRING)
	private TipoEntrega tipoEntrega;

	private BigDecimal valor;

	@ManyToOne
	@JoinColumn(name = "operacion_id", nullable = false)
	private Operacion operacion;

	@Enumerated(EnumType.STRING)
	private TipoPago tipoPago; // ORIGEN o CONVERSION

}
