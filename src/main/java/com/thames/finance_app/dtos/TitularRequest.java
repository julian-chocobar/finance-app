package com.thames.finance_app.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TitularRequest {

    @NotNull(message = "El nombre es obligatorio.")
    private String nombre;
    private String telefono;
    private String email;
    private String direccion;

}
