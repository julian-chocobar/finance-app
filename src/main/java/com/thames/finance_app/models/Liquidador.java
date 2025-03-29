package com.thames.finance_app.models;

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
@Table(name = "liquidadores")
@Setter
@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Liquidador {
	@Id
	@GeneratedValue(
			strategy = GenerationType.SEQUENCE,
			generator = "liquidador_generator")
	@SequenceGenerator (
			name = "liquidador_generator",
			sequenceName = "liquidador_sequence",
			allocationSize = 1)
	private Long id;

	private String nombre;

}
