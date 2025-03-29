package com.thames.finance_app.config;


import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode;

@EnableSpringDataWebSupport(pageSerializationMode = PageSerializationMode.VIA_DTO)
public class WebConfig {

//	@Override
//    public void addArgumentResolvers(@NonNull List<HandlerMethodArgumentResolver> argumentResolvers) {
//        PageableHandlerMethodArgumentResolver resolver = new PageableHandlerMethodArgumentResolver();
//        resolver.setOneIndexedParameters(true);
//        resolver.setPrefix("cajas");
//        argumentResolvers.add(resolver);
//
//        PageableHandlerMethodArgumentResolver movimientosResolver = new PageableHandlerMethodArgumentResolver();
//        movimientosResolver.setOneIndexedParameters(true);
//        movimientosResolver.setPrefix("movimientos");
//        argumentResolvers.add(movimientosResolver);
//    }
}
