package com.example.erp.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.erp.models.Usuario;
import com.example.erp.services.UsuarioService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/api/usuario")
public class ControllerUsuario {

    private final UsuarioService usuarioService;

    public ControllerUsuario(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/vista")
    public String usuarioVista(Model model, 
                                HttpSession session, 
                                @RequestParam(required = false) String resultado) {

        boolean acceso = usuarioService.validarAcceso(session, "usuario");
        if (acceso) {
            model.addAttribute("usuarioSesion", usuarioService.obtenerDeSesion(session));
            model.addAttribute("nuevoUsuario", new Usuario());
            model.addAttribute("usuarios", usuarioService.listarUsuarios());
            model.addAttribute("resultado", resultado);
            
            return "usuario";
        } 
    
        return "redirect:/api/login/vista";
    }

    @PostMapping("/crear")
    public String crearUsuario(@ModelAttribute Usuario nuevoUsuario,
                                HttpSession session) {

        boolean creado = usuarioService.crearUsuario(nuevoUsuario);
        return "redirect:/api/usuario/vista?resultado=" + (creado ? "ok" : "error");

    }
}
