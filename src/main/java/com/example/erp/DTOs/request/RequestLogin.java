package com.example.erp.DTOs.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestLogin {

    @NotBlank(message = "Usuario esta vacio")
    private String usuario;

    @NotBlank(message = "Contraseña esta vacio")
    private String contrasena;

}
