package com.example.erp.controllers;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.erp.models.Usuario;
import com.example.erp.services.UsuarioService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/api/login")
public class ControllerLogin {

    private final UsuarioService usuarioService;

    public ControllerLogin(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/vista")
    public String autenticacionVista(Model model, HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
        model.addAttribute("usuario", new Usuario());
        return "login";
    }

    @PostMapping("/autenticar")
    public String autenticacionUsuario(@ModelAttribute Usuario usuario, HttpSession session) {

        Optional<Usuario> usuarioOptional = usuarioService.autenticar(usuario.getNombre(), usuario.getContrasena());

        if (usuarioOptional.isPresent()) {
            Usuario usuarioAutenticado = usuarioOptional.get();
            usuarioService.guardarEnSesion(session, usuarioAutenticado);
            return "redirect:/api/venta/vista";
        }
        
        return "login";
    }

}
