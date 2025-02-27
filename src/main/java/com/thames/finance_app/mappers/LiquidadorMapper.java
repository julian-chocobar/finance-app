package com.thames.finance_app.mappers;

import org.springframework.stereotype.Component;

import com.thames.finance_app.dtos.LiquidadorRequest;
import com.thames.finance_app.dtos.LiquidadorResponse;
import com.thames.finance_app.models.Liquidador;

@Component
public class LiquidadorMapper {
    public Liquidador toEntity(LiquidadorRequest request) {
        return Liquidador.builder()
                .nombre(request.getNombre())
                .build();
    }

    public LiquidadorResponse toResponse(Liquidador liquidador) {
        LiquidadorResponse response = new LiquidadorResponse();
        response.setId(liquidador.getId());
        response.setNombre(liquidador.getNombre());
        return response;
    }
}
