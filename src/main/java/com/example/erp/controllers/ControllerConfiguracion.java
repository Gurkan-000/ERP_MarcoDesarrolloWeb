package com.example.erp.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/configuracion")
public class ControllerConfiguracion {

    @GetMapping("/vista")
    public String configuracionVista() {
        return "configuracion";
    }
}
