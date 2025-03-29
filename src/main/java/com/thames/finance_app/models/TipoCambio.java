package com.thames.finance_app.models;


import java.math.BigDecimal;

import jakarta.persistence.Entity;
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
@Table(name = "tipo_cambio")
@Setter
@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TipoCambio {

	@Id
	@GeneratedValue(
			strategy = GenerationType.SEQUENCE,
			generator = "tipo_cambio_generator")
	@SequenceGenerator (
			name = "tipo_cambio_generator",
			sequenceName = "tipo_cambio_sequence",
			allocationSize = 1)
	private Long id;

    @ManyToOne
    @JoinColumn(name = "moneda_origen_id", nullable = false)
    private Moneda monedaOrigen;

    @ManyToOne
    @JoinColumn(name = "moneda_destino_id", nullable = false)
    private Moneda monedaDestino;

    private BigDecimal valorCompra; // Valor de compra de la monedaOrigen en términos de monedaDestino
    private BigDecimal valorVenta; // Valor de venta de la monedaOrigen en términos de monedaDestino

}
