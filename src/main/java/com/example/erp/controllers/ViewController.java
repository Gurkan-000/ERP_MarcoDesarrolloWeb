package com.example.erp.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.erp.services.UsuarioService;

import jakarta.servlet.http.HttpSession;

@Controller
public class ViewController {

    private final UsuarioService usuarioService;

    public ViewController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/")
    public String index(HttpSession session) {
        if (usuarioService.obtenerDeSesion(session) == null) {
            return "redirect:/login/vista";
        }
        return "redirect:/venta";
    }

    @GetMapping("/venta")
    public String venta(Model model, HttpSession session) {
        if (!usuarioService.validarAcceso(session, "venta")) {
            return "redirect:/login/vista";
        }
        model.addAttribute("usuarioSesion", usuarioService.obtenerDeSesion(session));
        return "venta";
    }

    @GetMapping("/caja")
    public String caja(Model model, HttpSession session) {
        if (!usuarioService.validarAcceso(session, "caja")) {
            return "redirect:/login/vista";
        }
        model.addAttribute("usuarioSesion", usuarioService.obtenerDeSesion(session));
        return "caja";
    }

    @GetMapping("/inventario")
    public String inventario(Model model, HttpSession session) {
        if (!usuarioService.validarAcceso(session, "inventario")) {
            return "redirect:/login/vista";
        }
        model.addAttribute("usuarioSesion", usuarioService.obtenerDeSesion(session));
        return "inventario";
    }

    @GetMapping("/catalogo/productos")
    public String catalogoProductos(Model model, HttpSession session) {
        if (!usuarioService.validarAcceso(session, "catalogo")) {
            return "redirect:/login/vista";
        }
        model.addAttribute("usuarioSesion", usuarioService.obtenerDeSesion(session));
        return "catalogo/catalogoProducto";
    }

    @GetMapping("/catalogo/categorias")
    public String catalogoCategorias(Model model, HttpSession session) {
        if (!usuarioService.validarAcceso(session, "catalogo")) {
            return "redirect:/login/vista";
        }
        model.addAttribute("usuarioSesion", usuarioService.obtenerDeSesion(session));
        return "catalogo/catalogoCategoria";
    }

    @GetMapping("/usuarios")
    public String usuarios(Model model, HttpSession session) {
        if (!usuarioService.validarAcceso(session, "usuario")) {
            return "redirect:/login/vista";
        }
        model.addAttribute("usuarioSesion", usuarioService.obtenerDeSesion(session));
        return "usuario";
    }

    @GetMapping("/configuracion")
    public String configuracion(Model model, HttpSession session) {
        if (!usuarioService.validarAcceso(session, "configuracion")) {
            return "redirect:/login/vista";
        }
        model.addAttribute("usuarioSesion", usuarioService.obtenerDeSesion(session));
        return "configuracion";
    }
}
