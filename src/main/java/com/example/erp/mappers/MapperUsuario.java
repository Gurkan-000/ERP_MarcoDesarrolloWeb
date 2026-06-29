package com.example.erp.mappers;

import com.example.erp.DTOs.request.RequestRegister;
import com.example.erp.DTOs.response.ResponseUsuario;
import com.example.erp.entities.Usuario;
import com.example.erp.entities.enums.Rol;

public class MapperUsuario {

    public static Usuario toEntity(RequestRegister requestRegister, String contrasenaEncryptada, String rol) {

        return Usuario.builder()
                .nombre(requestRegister.getNombre())
                .usuario(requestRegister.getUsuario())
                .contrasena(contrasenaEncryptada)
                .rol(Rol.valueOf(rol))
                .build();

    }

    public static ResponseUsuario toDTO(Usuario usuario){

        return ResponseUsuario.builder()
        .nombre(usuario.getNombre())
        .usuario(usuario.getUsuario())
        .rol(usuario.getRol().name())
        .build();

    }

}
