package com.example.erp.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.erp.entities.MovimientoCaja;

import java.util.List;

import com.example.erp.entities.Caja;


public interface MovimientoCajaRepository extends  JpaRepository<MovimientoCaja, UUID>{

    public List <MovimientoCaja> findByCaja(Caja caja);


}