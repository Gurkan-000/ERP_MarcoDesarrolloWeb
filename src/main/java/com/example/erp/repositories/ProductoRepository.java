package com.example.erp.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.erp.entities.Producto;

public interface ProductoRepository extends  JpaRepository<Producto, UUID>{
}
