package com.thames.finance_app.models;



import com.thames.finance_app.enums.TipoTitular;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "titulares")
@Setter
@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Titular {
	
	@Id
	@GeneratedValue(
			strategy = GenerationType.SEQUENCE,
			generator = "titulares_generator")
	@SequenceGenerator (
			name = "titulares_generator",
			sequenceName = "titulares_sequence",
			allocationSize = 1)
	private Long id;
	
	private TipoTitular tipo;
	
	private String nombre;
	
	private String telefono;
	
	private String email;
	
	private String direccion;
	
	@OneToOne(mappedBy = "titular", cascade = CascadeType.ALL)
    @ToString.Exclude // Excluye esta propiedad del método toString()
    @EqualsAndHashCode.Exclude // Excluye esta propiedad del hashCode() y equals()
	private CuentaCorriente cuentaCorriente;

}
