package com.example.erp.DTOs.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestAuthUsuario {

    @NotBlank(message = "Nombre de usuario no valido")
    private String nombre;

    @NotBlank(message = "Contraseña no valida")
    private String contrasena;

}
