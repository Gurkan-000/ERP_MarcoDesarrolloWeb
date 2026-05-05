package com.example.erp.controllers;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.erp.models.Categoria;
import com.example.erp.models.Producto;
import com.example.erp.services.CatalogoService;

@Controller
@RequestMapping("/api/catalogo")
public class ControllerCatalogo {

    private final CatalogoService catalogoService;

    public ControllerCatalogo(CatalogoService catalogoService) {
        this.catalogoService = catalogoService;
    }

    @GetMapping("/vistaProducto")
    public String vistaProducto(Model model) {

        model.addAttribute("producto", new Producto());

        return "catalogo/catalogoProducto";
    }

    @GetMapping("/vistaCategoria")
    public String vistaCatalogo(Model model) {

        model.addAttribute("categoria", new Categoria());

        return "catalogo/catalogoCategoria";
    }

    @ModelAttribute("productos")
    public List<Producto> listarProductos() {
        return catalogoService.obtenerProductos();
    }

    @ModelAttribute("categorias")
    public List<Categoria> listarCategorias() {
        return catalogoService.obtenerCategorias();
    }

    @PostMapping("/crearProducto")
    public String crearProducto(
            @ModelAttribute Producto producto,
            @RequestParam(name = "categoriaNombre", required = false) String categoriaNombre) {
        catalogoService.crearProducto(producto, categoriaNombre);

        return "redirect:/api/catalogo/vistaProducto";
    }

    @PostMapping("/crearCategoria")
    public String crearCategoria(@ModelAttribute Categoria categoria) {
        catalogoService.crearCategoria(categoria);

        return "redirect:/api/catalogo/vistaCategoria";
    }

}
