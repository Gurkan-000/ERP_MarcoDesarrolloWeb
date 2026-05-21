package com.example.erp.entities;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.validator.constraints.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = "Categorias"
)
public class Categoria {

    private UUID idCategoria;

    @Column(
            length = 30
    )
    private String nombre;

    @OneToMany(
        mappedBy="categoria"
    )
    private List<Producto> productos = new ArrayList<>();

    public Categoria(String nombre) {
        this.nombre = nombre;
    }

    public void agregarProducto(Producto producto) {
        productos.add(producto);
    }

}
