package com.thames.finance_app.mappers;

import org.springframework.stereotype.Component;

import com.thames.finance_app.dtos.MonedaDTO;
import com.thames.finance_app.models.Moneda;

@Component
public class MonedaMapper {
	
    public Moneda toEntity(MonedaDTO request) {
        return Moneda.builder()
                .nombre(request.getNombre())
                .build();
    }
    
    public MonedaDTO toDTO(Moneda moneda) {
        return MonedaDTO.builder()
                .nombre(moneda.getNombre())
                .build();
    }

    public void updateEntity(Moneda moneda, MonedaDTO request) {
        moneda.setNombre(request.getNombre());
    }
}