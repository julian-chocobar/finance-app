package com.thames.finance_app.models;

import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
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
	
	@OneToOne
    @JoinColumn(name = "moneda_id")
    @ToString.Exclude // Excluye esta propiedad del método toString()
    @EqualsAndHashCode.Exclude // Excluye esta propiedad del hashCode() y equals()
	private Moneda moneda;
	
	@Builder.Default
	@OneToMany(mappedBy = "caja", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MovimientoCaja> movimientos = new ArrayList<>();

}
