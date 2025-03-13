package com.thames.finance_app.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyJoinColumn;
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
@Table(name = "cuentas_corrientes")
@Setter
@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CuentaCorriente {
	
	@Id
	@GeneratedValue(
			strategy = GenerationType.SEQUENCE,
			generator = "cuentas_corrientes_generator")
	@SequenceGenerator (
			name = "cuentas_corrientes_generator",
			sequenceName = "cuentas_corrientes_sequence",
			allocationSize = 1)	
	private Long id;
	
	@OneToOne
    @JoinColumn(name = "titular_id")
    @ToString.Exclude // Excluye esta propiedad del método toString()
    @EqualsAndHashCode.Exclude // Excluye esta propiedad del hashCode() y equals()
	private Titular titular;
	
	@Builder.Default
	@ElementCollection
	@CollectionTable(name = "cuenta_corriente_saldos", joinColumns = @JoinColumn(name = "cuenta_corriente_id"))
	@MapKeyJoinColumn(name = "moneda_id") // Cambia @MapKeyColumn por @MapKeyJoinColumn
	@Column(name = "saldo")
    private Map<Moneda, BigDecimal> saldos = new HashMap<>();
		
	@Builder.Default
	@OneToMany(mappedBy = "cuentaCorriente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MovimientoCtaCte> movimientos = new ArrayList<>();
	

    public BigDecimal getSaldoPorMoneda(Moneda moneda) {
        return saldos.getOrDefault(moneda, BigDecimal.ZERO);
    }

    public void setSaldoPorMoneda(Moneda moneda, BigDecimal nuevoSaldo) {
        saldos.put(moneda, nuevoSaldo);
    }

    public void actualizarSaldo(Moneda moneda, BigDecimal nuevoSaldo) {
        saldos.put(moneda, nuevoSaldo);
    }


}
