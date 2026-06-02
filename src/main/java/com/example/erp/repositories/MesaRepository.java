package com.example.erp.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.erp.entities.Mesa;
import com.example.erp.entities.enums.EstadoMesa;

public interface MesaRepository extends JpaRepository<Mesa, UUID> {
    
    long countByEstado(EstadoMesa estado);

}