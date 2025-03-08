package com.thames.finance_app.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.thames.finance_app.enums.Moneda;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.MapKeyEnumerated;
import jakarta.persistence.OneToMany;
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
    @JoinColumn(name = "cliente_id")
	private Cliente cliente;
	
	@Builder.Default
    @ElementCollection
    @CollectionTable(name = "cuenta_corriente_saldos", joinColumns = @JoinColumn(name = "cuenta_corriente_id"))
    @MapKeyEnumerated(EnumType.STRING)
    @MapKeyColumn(name = "moneda")
    @Column(name = "saldo")
    private Map<Moneda, BigDecimal> saldos = new HashMap<>();
	
//	@Builder.Default
//	@OneToMany(mappedBy = "cuentaCorriente", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Operacion> operaciones = new ArrayList<>();
	
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
