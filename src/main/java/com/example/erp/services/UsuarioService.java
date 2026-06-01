package com.example.erp.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.erp.DTOs.request.RequestAuthUsuario;
import com.example.erp.DTOs.request.RequestUsuario;
import com.example.erp.DTOs.response.ResponseUsuario;
import com.example.erp.entities.Usuario;
import com.example.erp.entities.enums.Rol;
import com.example.erp.exceptions.AutenticacionException;
import com.example.erp.exceptions.EntidadNoEncontradaException;

@Service
public class UsuarioService {

    private final List<Usuario> usuarios = new ArrayList<>();

    private Usuario usuarioActivo = null;

    public UsuarioService() {
        cargarUsuariosIniciales();
    }

    public Optional<Usuario> autenticar(String nombre, String contraseña) {

        return usuarios.stream()
                .filter(u -> nombre.equals(u.getNombre()) && contraseña.equals(u.getContrasena()))
                .findFirst();
    }

    public List<ResponseUsuario> obtenerUsuarios() {
        return usuarios.stream().map(u -> ResponseUsuario.builder()
                .nombre(u.getNombre())
                .rol(u.getRol())
                .build())
                .toList();
    }

    private void cargarUsuariosIniciales() {

        usuarios.addAll(Arrays.asList(
                new Usuario("Jose", "admin123", Rol.Administrador),
                new Usuario("Marco", "caja123", Rol.Cajero),
                new Usuario("Miguel", "mesero123", Rol.Mesero)
        ));

    }

    public void autenticar(RequestAuthUsuario requestUsuario) {

        String nombre = requestUsuario.getNombre().trim();
        String contrasena = requestUsuario.getContrasena().trim();

        Usuario usuarioAuth = usuarios.stream()
                .filter(u -> nombre.equals(u.getNombre()) && contrasena.equals(u.getContrasena()))
                .findFirst()
                .orElseThrow(() -> new EntidadNoEncontradaException("Acceso Denegado"));

        usuarioActivo = usuarioAuth;

    }

    public ResponseUsuario insertarUsuario(RequestUsuario requestUsuario) {

        Usuario usuario = Usuario.builder()
                .nombre(requestUsuario.getNombre())
                .contrasena(requestUsuario.getContrasena())
                .rol(requestUsuario.getRol())
                .build();

        usuarios.add(usuario);

        return ResponseUsuario.builder()
                .nombre(usuario.getNombre())
                .rol(usuario.getRol())
                .build();
    }

    public ResponseUsuario obtenerSesionActiva() {

        if(usuarioActivo == null){
            throw new AutenticacionException("No existe sesion activa");
        }

        return ResponseUsuario.builder()
                .nombre(usuarioActivo.getNombre())
                .rol(usuarioActivo.getRol())
                .build();
    }

    public void cerrarSesion() {
        usuarioActivo = null;
    }

}
