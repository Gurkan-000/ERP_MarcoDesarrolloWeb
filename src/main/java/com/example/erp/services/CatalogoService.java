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

    public void reiniciarInventario() {
        productos.clear();
    }

    public List<Producto> obtenerProductos() {
        return productos;
    }

    public List<Categoria> obtenerCategorias() {
        return categorias;
    }

    public Producto buscarProductoPorNombre(String nombre) {
        final String buscado = (nombre != null && !nombre.trim().isEmpty()) ? nombre.trim() : null;
        Producto resultado = null;
        if (buscado != null) {
            resultado = productos.stream()
                    .filter(prod -> prod.getNombre().equalsIgnoreCase(buscado))
                    .findFirst()
                    .orElse(null);
        }

        return resultado;
    }

    public void descontarStock(String nombreProducto, int cantidad) {
        Producto producto = null;
        if (cantidad > 0) {
            producto = buscarProductoPorNombre(nombreProducto);
        }

        if (producto != null) {
            int nuevoStock = producto.getStock() - cantidad;
            producto.setStock(Math.max(nuevoStock,0));
        }
    }

    public void actualizarStock(String nombreProducto, int nuevoStock) {
        Producto producto = null;
        if (nuevoStock >= 0) {
            producto = buscarProductoPorNombre(nombreProducto);
        }

        if (producto != null) {
            producto.setStock(nuevoStock);
        }
    }

    public void crearProducto(Producto producto, String categoriaNombre) {
        Categoria categoria = buscarCategoriaPorNombre(categoriaNombre);
        if (categoria != null) {
            producto.setCategoria(categoria);
            producto.setStock(Math.max(producto.getStock(), 1));
            categoria.agregarProducto(producto);
        }

        productos.add(producto);
    }

    public void crearCategoria(Categoria categoria) {
        categorias.add(categoria);
    }

    private Categoria buscarCategoriaPorNombre(String nombre) {
        final String buscada = (nombre != null && !nombre.trim().isEmpty()) ? nombre.trim() : null;
        Categoria resultado = null;
        if (buscada != null) {
            resultado = categorias.stream()
                    .filter(cat -> cat.getNombre().equalsIgnoreCase(buscada))
                    .findFirst()
                    .orElse(null);
        }

        return resultado;
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
