package com.example.erp.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/usuario")
public class ControllerUsuario {

    @GetMapping("/vista")
    public String usuarioVista() {
        return "usuario";
    }
}
