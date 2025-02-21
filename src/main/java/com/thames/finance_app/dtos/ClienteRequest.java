package com.thames.finance_app.dtos;

import lombok.Data;

@Data
public class ClienteRequest {
    private String nombre;
    private String telefono;
    private String email;
    private String direccion;

}
