package com.example.erp.DTOs.request;

import com.example.erp.entities.enums.Rol;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestUsuario {

    @NotBlank(message = "Nombre de usuario no valido")
    private String nombre;

    @NotBlank(message = "Contraseña no valida")
    private String contrasena;

    @NotNull(message = "Rol no valido")
    private Rol rol;

}
