package com.example.erp.entities;

import java.math.BigDecimal;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    @Column(
        precision=5,
        scale=1
    )
    private BigDecimal precio;
    
    private Integer stock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name="idCategoria"
    )
    private Categoria categoria;

}
