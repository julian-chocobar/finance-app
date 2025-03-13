package com.thames.finance_app.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TitularRequest {
	
    @NotNull(message = "El nombre es obligatorio.")
    private String nombre;  
    private String telefono;
    private String email;
    private String direccion;

}
