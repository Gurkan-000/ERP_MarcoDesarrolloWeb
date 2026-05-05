package com.example.erp.controllers;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.erp.models.Producto;
import com.example.erp.services.CatalogoService;

@Controller
@RequestMapping("/api/inventario")
public class ControllerInventario {

    private final CatalogoService catalogoService;

    public ControllerInventario(CatalogoService catalogoService) {
        this.catalogoService = catalogoService;
    }

    @GetMapping("/vista")
    public String inventarioVista() {
        return "inventario";
    }
    
    @ModelAttribute("productos")
    public List<Producto> listarProductos() {
        return catalogoService.obtenerProductos();
    }

    @PostMapping("/reiniciar")
    public String reiniciarInventario() {
        catalogoService.reiniciarInventario();

        return "redirect:/api/configuracion/vista";
    }

    @PostMapping("/stock/guardar")
    public String guardarStock(@RequestParam("productoNombre") List<String> productos,
            @RequestParam("nuevoStock") List<Integer> stocks) {
        int limite = Math.min(productos.size(), stocks.size());
        for (int i = 0; i < limite; i++) {
            catalogoService.actualizarStock(productos.get(i), stocks.get(i));
        }

        return "redirect:/api/inventario/vista";
    }

}
