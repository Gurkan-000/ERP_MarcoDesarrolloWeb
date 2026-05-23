package com.example.erp.entities;

import java.math.BigDecimal;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@Entity
@Table(
    name="Productos"
)
public class Producto {
    
    @Id
    @UuidGenerator
    private UUID idProducto;

    @Column(
        length=30
    )
    private String nombre;

    @ManyToOne
    @JoinColumn(
        name="idCategoria"
    )
    private Categoria categoria;

    @Column(
        precision=5,
        scale=2
    )
    private BigDecimal precio;

    private Integer stock;

    public Producto(String nombre, Categoria categoria, BigDecimal precio, Integer stock) {
        this.categoria = categoria;
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
    }

}
