package com.example.erp.controllers;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.erp.models.CajaEstado;
import com.example.erp.models.MovimientoCaja;
import com.example.erp.services.CajaService;

@Controller
@RequestMapping("/api/caja")
public class ControllerCaja {

    private final CajaService cajaService;

    public ControllerCaja(CajaService cajaService) {
        this.cajaService = cajaService;
    }

    @GetMapping("/vista")
    public String cajaVista(Model model) {
        model.addAttribute("registroCaja", new MovimientoCaja());
        model.addAttribute("aperturaCaja", new CajaEstado());

        return "caja";
    }

    @ModelAttribute("cajaEstado")
    public CajaEstado cajaEstado() {
        return cajaService.getEstado();
    }

    @ModelAttribute("movimientosCaja")
    public List<MovimientoCaja> listarMovimientos() {
        return cajaService.getMovimientos();
    }

    @PostMapping("/registrar")
    public String registrarMovimiento(@ModelAttribute("registroCaja") MovimientoCaja movimiento) {
        cajaService.registrarMovimiento(movimiento);

        return "redirect:/api/caja/vista";
    }

    @PostMapping("/cerrar")
    public String cerrarTurno() {
        cajaService.cerrarTurno();

        return "redirect:/api/caja/vista";
    }

    @PostMapping("/abrir")
    public String abrirTurno(@ModelAttribute("aperturaCaja") CajaEstado aperturaCaja) {
        cajaService.abrirTurno(aperturaCaja.getMontoInicial());

        return "redirect:/api/caja/vista";
    }
}
