package com.thames.finance_app.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "monedas")
@Setter
@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Moneda {
	@Id
	@GeneratedValue(
			strategy = GenerationType.SEQUENCE,
			generator = "moneda_generator")
	@SequenceGenerator (
			name = "moneda_generator",
			sequenceName = "moneda_sequence",
			allocationSize = 1)
	private Long id;
	
	private String nombre;
	
	@Column(unique = true, nullable = false)
    private String codigo; // USD, EUR, ARS, BRL.
	
}
