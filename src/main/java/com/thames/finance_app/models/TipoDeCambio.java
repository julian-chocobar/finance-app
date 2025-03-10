package com.thames.finance_app.models;


import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.thames.finance_app.enums.Moneda;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tipo_de_cambio")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TipoDeCambio {
	
	@Id
	@GeneratedValue(
			strategy = GenerationType.SEQUENCE,
			generator = "tipo_de_cambio_generator")
	@SequenceGenerator (
			name = "tipo_de_cambio_generator",
			sequenceName = "tipo_de_cambio_sequence",
			allocationSize = 1)
	private Long id;

	private Moneda moneda;
	
	private BigDecimal valorCompra;
	
	private BigDecimal valorVenta;
	
	private LocalDateTime ultimaActualizacion;
	
}
