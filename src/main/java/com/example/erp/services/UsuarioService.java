package com.example.erp.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.erp.models.Rol;
import com.example.erp.models.Usuario;

import jakarta.servlet.http.HttpSession;

@Service
public class UsuarioService {

    private final List<Usuario> usuarios = new ArrayList<>();

    public UsuarioService() {
        cargarUsuariosIniciales();
    }

    public Optional<Usuario> autenticar(String nombre, String contraseña) {

        return usuarios.stream()
                .filter(u -> nombre.equals(u.getNombre()) && contraseña.equals(u.getContrasena()))
                .findFirst();
    }

    public List<Usuario> listarUsuarios() {
        return usuarios;
    }

    public boolean validarCampos(Usuario usuario) {

        return usuario.getNombre() != null && !usuario.getNombre().isBlank()
            && usuario.getContrasena() != null && !usuario.getContrasena().isBlank()
            && usuario.getRol() != null;
            
    }

    public boolean crearUsuario(Usuario usuario) {

        if (validarCampos(usuario)) {

            if (usuario.getRol() == Rol.Mesero) {
                usuario.setVistas(Arrays.asList("venta"));
                usuarios.add(usuario);
            }

            if (usuario.getRol() == Rol.Cajero) {
                usuario.setVistas(Arrays.asList("venta", "caja"));
                usuarios.add(usuario);
            }

            return true;

        }

        return false;
    }

    public void guardarEnSesion(HttpSession session, Usuario usuario) {
        if (session != null) {
            session.setAttribute("usuarioSesion", usuario);
        }
    }

    public Usuario obtenerDeSesion(HttpSession session) {
        if (session == null) {
            return null;
        }

        return (Usuario) session.getAttribute("usuarioSesion");
    }

    public boolean validarAcceso(HttpSession session, String vista) {
        Usuario usuario = obtenerDeSesion(session);
        return usuario != null && usuario.getVistas() != null && usuario.getVistas().contains(vista);
    }

    private void cargarUsuariosIniciales() {

        usuarios.addAll(Arrays.asList(
            new Usuario("Jose", "admin123", Rol.Administrador, Arrays.asList("venta", "caja", "inventario", "catalogo", "usuario", "configuracion")),
            new Usuario("Marco", "caja123", Rol.Cajero, Arrays.asList("venta", "caja")),
            new Usuario("Miguel", "mesero123", Rol.Mesero, Arrays.asList("venta"))
        ));

    }

}
