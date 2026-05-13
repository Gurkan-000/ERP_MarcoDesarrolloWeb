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
import com.example.erp.services.UsuarioService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/api/caja")
public class ControllerCaja {

    private final CajaService cajaService;
    private final UsuarioService usuarioService;

    public ControllerCaja(CajaService cajaService, UsuarioService usuarioService) {
        this.cajaService = cajaService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/vista")
    public String cajaVista(Model model, HttpSession session) {

        boolean acceso = usuarioService.validarAcceso(session, "caja");

        if (acceso) {
            model.addAttribute("usuarioSesion", usuarioService.obtenerDeSesion(session));
            model.addAttribute("registroCaja", new MovimientoCaja());
            model.addAttribute("aperturaCaja", new CajaEstado());

            return "caja";
        }

        return "redirect:/api/login/vista";
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
