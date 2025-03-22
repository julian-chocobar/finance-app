package com.thames.finance_app.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http ) throws Exception {
		http
		.csrf(csrf -> csrf.disable())  // Deshabilita CSRF
        .cors(cors -> cors.configure(http))  // Habilita CORS
        .authorizeHttpRequests(request -> 
            request
            	.requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                .requestMatchers("/clientes/**").permitAll()
                .requestMatchers("/referidos/**").permitAll()
                .requestMatchers("/operaciones/**").permitAll()
                .requestMatchers("/ctas-ctes/**").permitAll()
                .requestMatchers("/cajas/**").permitAll()
                .requestMatchers("/tipoCambio/**").permitAll()
                
                
                
        );

		return http.build();

	}
	
    @Bean
    CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
//        config.setAllowedOrigins(List.of("http://localhost:5173")); // Permite el frontend
        config.setAllowedOrigins(List.of("*")); // Permite el frontend
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

	


}