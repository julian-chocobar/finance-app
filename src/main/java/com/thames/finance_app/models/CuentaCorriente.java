package com.thames.finance_app.models;

import java.math.BigDecimal;
import java.util.List;

import com.thames.finance_app.enums.Moneda;

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
	
	private BigDecimal saldoPesos;
	
	private BigDecimal saldoDolares;
	
	private BigDecimal saldoReales;
	
	private BigDecimal saldoCrypto;
	
	private BigDecimal saldoEuros;
	
//	@OneToMany(mappedBy = "cuentaCorriente", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Operacion> operaciones;
	
	@OneToMany(mappedBy = "cuentaCorriente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MovimientoCtaCte> movimientos;

    public BigDecimal getSaldoPorMoneda(Moneda moneda) {
        return switch (moneda) {
            case PESO -> saldoPesos;
            case USD -> saldoDolares;
            case REAL -> saldoReales;
            case EURO -> saldoEuros;
            case CRYPTO -> saldoCrypto;
		default -> throw new IllegalArgumentException("Unexpected value: " + moneda);
        };
    }

    public void setSaldoPorMoneda(Moneda moneda, BigDecimal nuevoSaldo) {
        switch (moneda) {
            case PESO -> saldoPesos = nuevoSaldo;
            case USD -> saldoDolares = nuevoSaldo;
            case REAL -> saldoReales = nuevoSaldo;
            case EURO -> saldoEuros= nuevoSaldo;
            case CRYPTO -> saldoCrypto =nuevoSaldo;
		default -> throw new IllegalArgumentException("Unexpected value: " + moneda);
        }
    }

}
