package com.thames.finance_app.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.thames.finance_app.enums.TipoMovimiento;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "movimientos_cta_cte")
@Setter
@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoCtaCte {
	
	@Id
	@GeneratedValue(
			strategy = GenerationType.SEQUENCE,
			generator = "movimientos_cta_cte_generator")
	@SequenceGenerator (
			name = "movimientos_cta_cte_generator",
			sequenceName = "movimientos_cta_cte_sequence",
			allocationSize = 1)
	private Long id;
	
	private LocalDateTime fecha;
	
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoMovimiento tipoMovimiento;
	 
	@ManyToOne
    @JoinColumn(name = "cuenta_corriente_id", nullable = true)
    private CuentaCorriente cuentaCorriente; 
	
	@ManyToOne
    @JoinColumn(name = "operacion_id", nullable = true)
    private Operacion operacion;
	
    @ManyToOne
    @JoinColumn(name = "moneda_id", nullable = false)
    private Moneda moneda; 

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal monto;
    

}
