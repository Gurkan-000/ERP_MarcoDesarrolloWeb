package com.example.erp.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.erp.entities.Caja;

public interface CajaRepository extends  JpaRepository<Caja, UUID>{

}
