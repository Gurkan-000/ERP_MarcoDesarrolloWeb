package com.example.erp.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(
        name = "Categorias"
)
public class Categoria {

    @Id
    @UuidGenerator
    private UUID idCategoria;

    @Column(
            length = 30
    )
    private String nombre;

    @OneToMany(
            mappedBy = "categoria",
            fetch=FetchType.LAZY,
            cascade=CascadeType.REMOVE
    )
    private final List<Producto> productos = new ArrayList<>();

    public void addProducto(Producto producto){
        producto.setCategoria(this);
        productos.add(producto);
    }

}
