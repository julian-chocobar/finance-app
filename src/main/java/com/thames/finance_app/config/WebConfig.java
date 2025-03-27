package com.thames.finance_app.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.lang.NonNull;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer{

	@Override
    public void addArgumentResolvers(@NonNull List<HandlerMethodArgumentResolver> argumentResolvers) {
        PageableHandlerMethodArgumentResolver resolver = new PageableHandlerMethodArgumentResolver();
        resolver.setOneIndexedParameters(true);
        resolver.setPrefix("cajas");
        argumentResolvers.add(resolver);
        
        PageableHandlerMethodArgumentResolver movimientosResolver = new PageableHandlerMethodArgumentResolver();
        movimientosResolver.setOneIndexedParameters(true);
        movimientosResolver.setPrefix("movimientos");
        argumentResolvers.add(movimientosResolver);
    }
}
