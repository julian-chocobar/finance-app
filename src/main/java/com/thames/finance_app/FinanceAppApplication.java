package com.thames.finance_app;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.thames.finance_app.enums.TipoTitular;
import com.thames.finance_app.models.Caja;
import com.thames.finance_app.models.Titular;
import com.thames.finance_app.models.CuentaCorriente;
import com.thames.finance_app.models.Moneda;
import com.thames.finance_app.models.TipoCambio;
import com.thames.finance_app.repositories.CajaRepository;
import com.thames.finance_app.repositories.TitularRepository;
import com.thames.finance_app.repositories.CtaCteRepository;
import com.thames.finance_app.repositories.MonedaRepository;
import com.thames.finance_app.repositories.TipoCambioRepository;

@SpringBootApplication
public class FinanceAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(FinanceAppApplication.class, args);
	}
	
	@SuppressWarnings("unused")
	@Bean
    CommandLineRunner init(CajaRepository cajaRepository, TipoCambioRepository tipoCambioRepository,
    						TitularRepository clienteRepository, CtaCteRepository ctaCteRepository,
    						MonedaRepository monedaRepository) {
        return args -> {
        	        	       	
        	// CREAR MONEDAS        	
        	Moneda dolar = Moneda.builder().nombre("USD").codigo("USD").build();  
        	monedaRepository.save(dolar);
        	
        	Moneda peso = Moneda.builder().nombre("PESO").codigo("ARS").build();  
        	monedaRepository.save(peso);
        	
        	Moneda euro = Moneda.builder().nombre("EURO").codigo("EUR").build();
        	monedaRepository.save(euro);
        	
        	Moneda real = Moneda.builder().nombre("REAL").codigo("BRL").build();
        	monedaRepository.save(real);
        	
        	//CREAR TIPOS DE CAMBIO
        	tipoCambioRepository.saveAll(crearTiposCambioDePrueba(dolar,peso,euro,real));
        	
     
        	//CREAR CAJAS
        	Caja cajaPesos = Caja.builder()
        					.nombre("PESOS")
        					.saldoReal(BigDecimal.ZERO)
        					.saldoDisponible(BigDecimal.ZERO)
        					.moneda(peso)
        					.build();
        	cajaRepository.save(cajaPesos);
        	
        	Caja cajaDolares = Caja.builder()
        			.nombre("DOLARES")
					.saldoReal(BigDecimal.ZERO)
					.saldoDisponible(BigDecimal.ZERO)
					.moneda(dolar)
					.build();
        	cajaRepository.save(cajaDolares);
        	
        	Caja cajaReales = Caja.builder()
        			.nombre("REALES")
					.saldoReal(BigDecimal.ZERO)
					.saldoDisponible(BigDecimal.ZERO)
					.moneda(real)
					.build();
        	cajaRepository.save(cajaReales);
        	
        	Caja cajaEuros = Caja.builder()
        			.nombre("EUROS")
					.saldoReal(BigDecimal.ZERO)
					.saldoDisponible(BigDecimal.ZERO)
					.moneda(euro)
					.build();
        	cajaRepository.save(cajaEuros);
        	   	
        	//CREAR CLIENTES Y CUENTAS CORRIENTES
        	
            List<Moneda> monedas = monedaRepository.findAll(); 

            // Inicializar los saldos con BigDecimal.ZERO para cada moneda
            Map<Moneda, BigDecimal> saldosIniciales = new HashMap<>();
            for (Moneda moneda : monedas) {
                saldosIniciales.put(moneda, BigDecimal.ZERO);
            }
        	
        	Titular juan = Titular.builder()
        			.tipo(TipoTitular.CLIENTE)
        			.nombre("Jose Suarez")
        			.email("juanperez@gmail.com")
        			.direccion("Calle False 123")
        			.build();  
        	 clienteRepository.save(juan);
    	    CuentaCorriente cuentaJuan = CuentaCorriente.builder()
    	            .titular(juan)
    	            .saldos(saldosIniciales)
    	            .build();
    	    juan.setCuentaCorriente(cuentaJuan);
    	    ctaCteRepository.save(cuentaJuan);
    	    clienteRepository.save(juan);
  
    	    
    	    
    	    Titular pedro = Titular.builder()
        			.tipo(TipoTitular.REFERIDO)
        			.nombre("Pedro Lopez")
        			.email("pedrolo@gmail.com")
        			.direccion("Alberdi 444")
        			.build();
    	    clienteRepository.save(pedro);
    	    CuentaCorriente cuentaPedro = CuentaCorriente.builder()
    	            .titular(pedro)
    	            .saldos(saldosIniciales)
    	            .build();
    	    pedro.setCuentaCorriente(cuentaPedro);
    	    ctaCteRepository.save(cuentaPedro);
    	    clienteRepository.save(pedro);
        	        	
        };
        
	}
	
    private static List<TipoCambio> crearTiposCambioDePrueba(Moneda usd, 
    														Moneda peso,
    														Moneda euro,
    														Moneda real) {
        return Arrays.asList(
                // USD → ARS
                TipoCambio.builder()
                        .monedaOrigen(usd)
                        .monedaDestino(peso)
                        .valorCompra(new BigDecimal("1205"))
                        .valorVenta(new BigDecimal("1220"))
                        .build(),

                // ARS → USD
                TipoCambio.builder()
                        .monedaOrigen(peso)
                        .monedaDestino(usd)
                        .valorCompra(new BigDecimal("0.00082"))
                        .valorVenta(new BigDecimal("0.00080"))
                        .build(),

                // EUR → USD
                TipoCambio.builder()
                        .monedaOrigen(euro)
                        .monedaDestino(usd)
                        .valorCompra(new BigDecimal("1.10"))
                        .valorVenta(new BigDecimal("1.15"))
                        .build(),

                // USD → EUR
                TipoCambio.builder()
                        .monedaOrigen(usd)
                        .monedaDestino(euro)
                        .valorCompra(new BigDecimal("0.85"))
                        .valorVenta(new BigDecimal("0.90"))
                        .build(),

                // BRL → ARS
                TipoCambio.builder()
                        .monedaOrigen(real)
                        .monedaDestino(peso)
                        .valorCompra(new BigDecimal("220"))
                        .valorVenta(new BigDecimal("230"))
                        .build(),

                // ARS → BRL
                TipoCambio.builder()
                        .monedaOrigen(peso)
                        .monedaDestino(real)
                        .valorCompra(new BigDecimal("0.0045"))
                        .valorVenta(new BigDecimal("0.0043"))
                        .build()
        );
    }

}
