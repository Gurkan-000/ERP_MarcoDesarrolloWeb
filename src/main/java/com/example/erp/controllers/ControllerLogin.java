package com.example.erp.controllers;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.erp.entities.Usuario;
import com.example.erp.services.UsuarioService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/login")
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
    public String autenticacionUsuario(
            @RequestParam String nombre, 
            @RequestParam String contrasena, 
            HttpSession session, 
            Model model) {

        Optional<Usuario> usuarioOptional = usuarioService.autenticar(nombre, contrasena);

        if (usuarioOptional.isPresent()) {
            Usuario usuarioAutenticado = usuarioOptional.get();
            usuarioService.guardarEnSesion(session, usuarioAutenticado);
            return "redirect:/venta";
        }
        
        // Si las credenciales fallan, enviamos un aviso a la vista
        model.addAttribute("error", true);
        return "login";
    }
}