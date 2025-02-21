package com.thames.finance_app.models;

import java.math.BigDecimal;

import com.thames.finance_app.enums.Moneda;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cajas")
@Setter
@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Caja {
	
	@Id
	@GeneratedValue(
			strategy = GenerationType.SEQUENCE,
			generator = "caja_generator")
	@SequenceGenerator (
			name = "caja_generator",
			sequenceName = "caja_sequence",
			allocationSize = 1)
	
	private Long id;
	
	private BigDecimal saldoReal;
	
	private BigDecimal saldoDisponible;
	
	@Enumerated(EnumType.STRING)
	private Moneda moneda;
	
	

}
