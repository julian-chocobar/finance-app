package com.thames.finance_app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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
                .requestMatchers("/clientes/**").permitAll()
                .requestMatchers("/operaciones/**").permitAll()
                .requestMatchers("/ctas-ctes/**").permitAll()
                
        );

		return http.build();

	}
	
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")  // Permite CORS en todos los endpoints
                    .allowedOrigins("*")  // Permite solicitudes desde cualquier origen
                    .allowedMethods("GET", "POST", "PUT", "DELETE")  // Métodos permitidos
                    .allowedHeaders("*");  // Headers permitidos
            }
        };
    }
	


}