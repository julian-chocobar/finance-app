package com.thames.finance_app.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ClienteRequest {
	
    @NotNull(message = "El nombre es obligatorio.")
    private String nombre;
    
    private String telefono;
    private String email;
    private String direccion;


}
