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

import com.thames.finance_app.dtos.OperacionRequest;
import com.thames.finance_app.enums.TipoOperacion;
import com.thames.finance_app.enums.TipoTitular;
import com.thames.finance_app.models.Caja;
import com.thames.finance_app.models.CuentaCorriente;
import com.thames.finance_app.models.Moneda;
import com.thames.finance_app.models.TipoCambio;
import com.thames.finance_app.models.Titular;
import com.thames.finance_app.repositories.CajaRepository;
import com.thames.finance_app.repositories.CtaCteRepository;
import com.thames.finance_app.repositories.MonedaRepository;
import com.thames.finance_app.repositories.OperacionRepository;
import com.thames.finance_app.repositories.TipoCambioRepository;
import com.thames.finance_app.repositories.TitularRepository;
import com.thames.finance_app.services.OperacionService;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class FinanceAppApplication {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.load();
		// Pasar variables al sistema (opcional, pero útil)
		System.setProperty("SP_PASSWORD", dotenv.get("SP_PASSWORD"));
		System.setProperty("DB_URL", dotenv.get("DB_URL"));
		System.setProperty("DB_USER_NAME", dotenv.get("DB_USER_NAME"));
		System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));

		SpringApplication.run(FinanceAppApplication.class, args);
	}

	@SuppressWarnings("unused")
	@Bean
    CommandLineRunner init(CajaRepository cajaRepository, TipoCambioRepository tipoCambioRepository,
    						TitularRepository clienteRepository, CtaCteRepository ctaCteRepository,
    						MonedaRepository monedaRepository, OperacionRepository operacionRepository,
    						OperacionService operacionService) {
        return args -> {

        	// CREAR MONEDAS
        	Moneda dolar = Moneda.builder().nombre("DOLAR").codigo("USD").build();
        	monedaRepository.save(dolar);

			Moneda dolarCaraChica = Moneda.builder().nombre("DOLARcc").codigo("USDcc").build();
        	monedaRepository.save(dolarCaraChica);

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

			Caja cajaDolaresCaraChica = Caja.builder()
        			.nombre("DOLAREScc")
					.saldoReal(BigDecimal.ZERO)
					.saldoDisponible(BigDecimal.ZERO)
					.moneda(dolarCaraChica)
					.build();
        	cajaRepository.save(cajaDolaresCaraChica);

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
        			.nombre("Juan Suarez")
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


    	    List<Titular> referidos = List.of(
    	    	    Titular.builder().tipo(TipoTitular.REFERIDO).nombre("Pedro Lopez").email("pedrolo@gmail.com").telefono("+54 9 11 1234 5678").direccion("Alberdi 444").build(),
    	    	    Titular.builder().tipo(TipoTitular.REFERIDO).nombre("Maria Gonzalez").email(null).telefono("+54 9 11 2345 6789").direccion("San Martin 123").build(),
    	    	    Titular.builder().tipo(TipoTitular.REFERIDO).nombre("Carlos Perez").email("carlosperez@gmail.com").telefono("+54 9 11 3456 7890").direccion("Belgrano 987").build(),
    	    	    Titular.builder().tipo(TipoTitular.REFERIDO).nombre("Lucia Ramirez").email("luciar@gmail.com").telefono("+54 9 11 4567 8901").direccion("Av. Rivadavia 321").build(),
    	    	    Titular.builder().tipo(TipoTitular.REFERIDO).nombre("Jorge Diaz").email("jorgediaz@gmail.com").telefono("+54 9 11 5678 9012").direccion("Colon 555").build(),
    	    	    Titular.builder().tipo(TipoTitular.REFERIDO).nombre("Ana Torres").email("anatorres@gmail.com").telefono("+54 9 11 6789 0123").direccion("Sarmiento 777").build(),
    	    	    Titular.builder().tipo(TipoTitular.REFERIDO).nombre("Fernando Lopez").email("f.lopez@gmail.com").telefono("+54 9 11 7890 1234").direccion("Mitre 654").build(),
    	    	    Titular.builder().tipo(TipoTitular.REFERIDO).nombre("Sofia Martinez").email("sofia.mtz@gmail.com").telefono("+54 9 11 8901 2345").direccion("Santa Fe 852").build(),
    	    	    Titular.builder().tipo(TipoTitular.REFERIDO).nombre("Raul Castro").email("raul.castro@gmail.com").telefono("+54 9 11 9012 3456").direccion("Entre Rios 369").build(),
    	    	    Titular.builder().tipo(TipoTitular.REFERIDO).nombre("Valeria Nuñez").email("valeria.n@gmail.com").telefono("+54 9 11 0123 4567").direccion("Corrientes 741").build(),
    	    	    Titular.builder().tipo(TipoTitular.REFERIDO).nombre("Esteban Vega").email("esteban.vega@gmail.com").telefono("+54 9 11 1234 5678").direccion("Tucuman 159").build(),
    	    	    Titular.builder().tipo(TipoTitular.REFERIDO).nombre("Marina Rojas").email("marina.rojas@gmail.com").telefono("+54 9 11 2345 6789").direccion("Moreno 753").build(),
    	    	    Titular.builder().tipo(TipoTitular.REFERIDO).nombre("Gustavo Farias").email("gustavo.f@gmail.com").telefono("+54 9 11 3456 7890").direccion("Catamarca 268").build(),
    	    	    Titular.builder().tipo(TipoTitular.REFERIDO).nombre("Diana Salinas").email("diana.salinas@gmail.com").telefono("+54 9 11 4567 8901").direccion("9 de Julio 432").build(),
    	    	    Titular.builder().tipo(TipoTitular.REFERIDO).nombre("Hernan Mendez").email("hernan.mendez@gmail.com").telefono("+54 9 11 5678 9012").direccion("Alsina 990").build(),
    	    	    Titular.builder().tipo(TipoTitular.REFERIDO).nombre("Beatriz Suarez").email("beatriz.suarez@gmail.com").telefono("+54 9 11 6789 0123").direccion("Lavalle 876").build(),
    	    	    Titular.builder().tipo(TipoTitular.REFERIDO).nombre("Oscar Medina").email("oscar.medina@gmail.com").telefono("+54 9 11 7890 1234").direccion("Urquiza 345").build(),
    	    	    Titular.builder().tipo(TipoTitular.REFERIDO).nombre("Natalia Herrera").email("natalia.herrera@gmail.com").telefono("+54 9 11 8901 2345").direccion("Mendoza 258").build()
//    	    	    Titular.builder().tipo(TipoTitular.REFERIDO).nombre("Ricardo Ponce").email("ricardo.ponce@gmail.com").telefono("+54 9 11 9012 3456").direccion("Rioja 785").build(),
//    	    	    Titular.builder().tipo(TipoTitular.REFERIDO).nombre("Andrea Fernandez").email("andrea.fernandez@gmail.com").telefono("+54 9 11 0123 4567").direccion("Jujuy 632").build(),
//    	    	    Titular.builder().tipo(TipoTitular.REFERIDO).nombre("Silvia Fernandez").email("andrea.fernandez@gmail.com").telefono("+54 9 11 0123 4567").direccion("Jujuy 632").build(),
//    	    	    Titular.builder().tipo(TipoTitular.REFERIDO).nombre("Nestor Fernandez").email("andrea.fernandez@gmail.com").telefono("+54 9 11 0123 4567").direccion("Jujuy 632").build()

    	    	);

    	    	for (Titular referido : referidos) {
    	    	    clienteRepository.save(referido);
    	    	    CuentaCorriente cuenta = CuentaCorriente.builder()
    	    	            .titular(referido)
    	    	            .saldos(saldosIniciales)
    	    	            .build();
    	    	    referido.setCuentaCorriente(cuenta);
    	    	    ctaCteRepository.save(cuenta);
    	    	    clienteRepository.save(referido);
    	    	}


    	    	OperacionRequest operacionRequest = OperacionRequest.builder()
    	    			.tipo(TipoOperacion.COMPRA)
    	    			.nombreCliente("Juan Suarez")
    	    			.monedaOrigen("USD")
    	    			.montoOrigen(new BigDecimal("1000"))
    	    			.valorTipoCambio(new BigDecimal("0.85"))
    	    			.monedaConversion("EUR")
    	    			.build();
    	       operacionService.crearOperacion(operacionRequest);
    	       operacionService.crearOperacion(operacionRequest);
    	       operacionService.crearOperacion(operacionRequest);
    	       operacionService.crearOperacion(operacionRequest);
    	       operacionService.crearOperacion(operacionRequest);
    	       operacionService.crearOperacion(operacionRequest);
    	       operacionService.crearOperacion(operacionRequest);
    	       operacionService.crearOperacion(operacionRequest);
    	       operacionService.crearOperacion(operacionRequest);
    	       operacionService.crearOperacion(operacionRequest);
    	       operacionService.crearOperacion(operacionRequest);
    	       operacionService.crearOperacion(operacionRequest);
    	       operacionService.crearOperacion(operacionRequest);
    	       operacionService.crearOperacion(operacionRequest);
    	       operacionService.crearOperacion(operacionRequest);

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
                        .monedaConversion(peso)
                        .valorCompra(new BigDecimal("1205"))
                        .valorVenta(new BigDecimal("1220"))
                        .build(),

                // ARS → USD
                TipoCambio.builder()
                        .monedaOrigen(peso)
                        .monedaConversion(usd)
                        .valorCompra(new BigDecimal("0.00082"))
                        .valorVenta(new BigDecimal("0.00080"))
                        .build(),

                // EUR → USD
                TipoCambio.builder()
                        .monedaOrigen(euro)
                        .monedaConversion(usd)
                        .valorCompra(new BigDecimal("1.10"))
                        .valorVenta(new BigDecimal("1.15"))
                        .build(),

                // USD → EUR
                TipoCambio.builder()
                        .monedaOrigen(usd)
                        .monedaConversion(euro)
                        .valorCompra(new BigDecimal("0.85"))
                        .valorVenta(new BigDecimal("0.90"))
                        .build(),

                // BRL → ARS
                TipoCambio.builder()
                        .monedaOrigen(real)
                        .monedaConversion(peso)
                        .valorCompra(new BigDecimal("220"))
                        .valorVenta(new BigDecimal("230"))
                        .build(),

                // ARS → BRL
                TipoCambio.builder()
                        .monedaOrigen(peso)
                        .monedaConversion(real)
                        .valorCompra(new BigDecimal("0.0045"))
                        .valorVenta(new BigDecimal("0.0043"))
                        .build()
        );
    }

}
