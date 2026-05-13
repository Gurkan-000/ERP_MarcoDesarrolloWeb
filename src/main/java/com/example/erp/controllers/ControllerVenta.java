package com.example.erp.controllers;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.erp.models.Mesa;
import com.example.erp.models.Pedido;
import com.example.erp.models.Producto;
import com.example.erp.models.Venta;
import com.example.erp.services.CatalogoService;
import com.example.erp.services.UsuarioService;
import com.example.erp.services.VentaService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/api/venta")
public class ControllerVenta {

    private final CatalogoService catalogoService;
    private final VentaService ventaService;
    private final UsuarioService usuarioService;

    public ControllerVenta(CatalogoService catalogoService, VentaService ventaService, UsuarioService usuarioService) {
        this.catalogoService = catalogoService;
        this.ventaService = ventaService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/vista")
    public String ventaVista(@RequestParam(required = false) Integer mesa, 
                            Model model, 
                            HttpSession session) {

        boolean acceso = usuarioService.validarAcceso(session, "venta");

        if (acceso) {
            model.addAttribute("usuarioSesion", usuarioService.obtenerDeSesion(session));
            model.addAttribute("pedidoMesa", new Pedido());
            model.addAttribute("pedidoAccion", new Pedido());
            model.addAttribute("pedidoCobro", new Pedido());
            model.addAttribute("pedidoRapido", new Pedido());
            model.addAttribute("mesaSeleccionada", mesa != null ? mesa : "");

            return "venta";
        }

        return "redirect:/api/login/vista";
    }

    @ModelAttribute("mesas")
    public List<Mesa> listarMesas() {
        return ventaService.obtenerMesas();
    }

    @ModelAttribute("productos")
    public List<Producto> listarProductos() {
        return catalogoService.obtenerProductos();
    }

    @ModelAttribute("ventas")
    public List<Venta> listarVentas() {
        return ventaService.obtenerVentas();
    }

    @PostMapping("/mesa/pedido")
    public String registrarPedidoMesa(@ModelAttribute Pedido pedidoMesa) {
        ventaService.agregarProductoMesa(pedidoMesa);

        return ventaService.redirigirMesa(pedidoMesa.getMesaNumero());
    }

    @PostMapping("/mesa/ocupar")
    public String ocuparMesa(@ModelAttribute Pedido pedidoAccion) {
        ventaService.marcarMesaOcupada(pedidoAccion.getMesaNumero());

        return "redirect:/api/venta/vista";
    }

    @PostMapping("/mesa/cobrar")
    public String cobrarMesa(@ModelAttribute Pedido pedidoCobro) {
        ventaService.cobrarMesa(pedidoCobro);

        return ventaService.redirigirMesa(pedidoCobro.getMesaNumero());
    }

    @PostMapping("/pedido/cobrar")
    public String cobrarPedidoRapido(@ModelAttribute Pedido pedidoRapido) {
        ventaService.cobrarPedidoRapido(pedidoRapido);

        return "redirect:/api/venta/vista";
    }

}
