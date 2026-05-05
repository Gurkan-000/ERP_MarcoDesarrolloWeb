package com.example.erp.controllers;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.erp.models.Mesa;
import com.example.erp.models.Pedido;
import com.example.erp.models.Producto;
import com.example.erp.models.Venta;
import com.example.erp.services.CatalogoService;
import com.example.erp.services.VentaService;

@Controller
@RequestMapping("/api/venta")
public class ControllerVenta {

    private final CatalogoService catalogoService;
    private final VentaService ventaService;

    public ControllerVenta(CatalogoService catalogoService, VentaService ventaService) {
        this.catalogoService = catalogoService;
        this.ventaService = ventaService;
    }

    @GetMapping("/vista")
    public String ventaVista(@RequestParam(name = "mesa", required = false) Integer mesa, Model model) {
        model.addAttribute("pedidoMesa", new Pedido());
        model.addAttribute("pedidoAccion", new Pedido());
        model.addAttribute("pedidoCobro", new Pedido());
        model.addAttribute("pedidoRapido", new Pedido());
        model.addAttribute("mesaSeleccionada", mesa != null ? mesa : "");

        return "venta";
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
    public String registrarPedidoMesa(@ModelAttribute("pedidoMesa") Pedido pedidoMesa) {
        ventaService.agregarProductoMesa(pedidoMesa);

        return redirigirMesa(pedidoMesa.getMesaNumero());
    }

    @PostMapping("/mesa/ocupar")
    public String ocuparMesa(@ModelAttribute("pedidoAccion") Pedido pedidoAccion) {
        ventaService.marcarMesaOcupada(pedidoAccion.getMesaNumero());

        return "redirect:/api/venta/vista";
    }

    @PostMapping("/mesa/cobrar")
    public String cobrarMesa(@ModelAttribute("pedidoCobro") Pedido pedidoCobro) {
        ventaService.cobrarMesa(pedidoCobro);

        return redirigirMesa(pedidoCobro.getMesaNumero());
    }

    @PostMapping("/pedido/cobrar")
    public String cobrarPedidoRapido(@ModelAttribute("pedidoRapido") Pedido pedidoRapido) {
        ventaService.cobrarPedidoRapido(pedidoRapido);

        return "redirect:/api/venta/vista";
    }

    private String redirigirMesa(int mesaNumero) {
        if (mesaNumero > 0) {
            return "redirect:/api/venta/vista?mesa=" + mesaNumero;
        }

        return "redirect:/api/venta/vista";
    }
}
