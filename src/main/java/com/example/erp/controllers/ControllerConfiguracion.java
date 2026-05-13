package com.example.erp.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.erp.services.UsuarioService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/api/configuracion")
public class ControllerConfiguracion {

    private final UsuarioService usuarioService;

    public ControllerConfiguracion(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/vista")
    public String configuracionVista(Model model, HttpSession session) {

        boolean acceso = usuarioService.validarAcceso(session, "configuracion");

        if (acceso) {
            model.addAttribute("usuarioSesion", usuarioService.obtenerDeSesion(session));
            return "configuracion";
        }

        return "redirect:/api/login/vista";

    }
}
