package com.thames.finance_app.models;



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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "clientes")
@Setter
@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {
	
	@Id
	@GeneratedValue(
			strategy = GenerationType.SEQUENCE,
			generator = "cliente_generator")
	@SequenceGenerator (
			name = "cliente_generator",
			sequenceName = "cliente_sequence",
			allocationSize = 1)
	private Long id;
	
	private boolean esReferido;
	
	private String nombre;
	
	private String telefono;
	
	private String email;
	
	private String direccion;
	
	@OneToOne(mappedBy = "cliente", cascade = CascadeType.ALL)
	private CuentaCorriente cuentaCorriente;

}
