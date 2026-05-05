package com.example.erp.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.erp.models.Categoria;
import com.example.erp.models.Producto;

@Service
public class CatalogoService {

    private final List<Producto> productos = new ArrayList<>();
    private final List<Categoria> categorias = new ArrayList<>();

    public CatalogoService() {
        cargarDatosIniciales();
    }

    public void reiniciarInventario(){
        productos.clear();
    }

    public List<Producto> obtenerProductos() {
        return productos;
    }

    public List<Categoria> obtenerCategorias() {
        return categorias;
    }

    public Producto buscarProductoPorNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return null;
        }

        String buscado = nombre.trim();
        return productos.stream()
                .filter(prod -> prod.getNombre().equalsIgnoreCase(buscado))
                .findFirst()
                .orElse(null);
    }

    public void descontarStock(String nombreProducto, int cantidad) {
        if (cantidad <= 0) {
            return;
        }

        Producto producto = buscarProductoPorNombre(nombreProducto);
        if (producto == null) {
            return;
        }

        int nuevoStock = producto.getStock() - cantidad;
        producto.setStock(Math.max(nuevoStock, 0));
    }

    public void actualizarStock(String nombreProducto, int nuevoStock) {
        if (nuevoStock < 0) {
            return;
        }

        Producto producto = buscarProductoPorNombre(nombreProducto);
        if (producto == null) {
            return;
        }

        producto.setStock(nuevoStock);
    }

    public void crearProducto(Producto producto, String categoriaNombre) {
        Categoria categoria = buscarCategoriaPorNombre(categoriaNombre);
        if (categoria != null) {
            producto.setCategoria(categoria);
            producto.setStock(0);
            categoria.agregarProducto(producto);
        }

        productos.add(producto);
    }

    public void crearCategoria(Categoria categoria) {
        categorias.add(categoria);
    }

    private Categoria buscarCategoriaPorNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return null;
        }

        String buscada = nombre.trim();
        return categorias.stream()
                .filter(cat -> cat.getNombre().equalsIgnoreCase(buscada))
                .findFirst()
                .orElse(null);
    }

    private void cargarDatosIniciales() {
        Categoria c1 = new Categoria("Broasters", "Activo");
        Categoria c2 = new Categoria("Bebidas", "Activo");

        Producto p1 = new Producto("Pecho", c1, 17, 5);
        Producto p2 = new Producto("Inka Cola 1.5 L", c2, 5.5, 10);

        c1.agregarProducto(p1);
        c2.agregarProducto(p2);

        categorias.add(c1);
        categorias.add(c2);
        productos.add(p1);
        productos.add(p2);
    }
}
