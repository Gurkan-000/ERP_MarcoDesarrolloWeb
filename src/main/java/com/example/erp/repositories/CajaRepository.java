package com.example.erp.repositories;


import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.erp.entities.Caja;
import com.example.erp.entities.enums.EstadoCaja;

public interface CajaRepository extends JpaRepository<Caja, UUID> {

    public boolean existsByEstado(EstadoCaja estado);

    public Optional<Caja> findByEstado(EstadoCaja estado);

    Optional<Caja> findTopByEstadoOrderByFechaCierreDesc(EstadoCaja estado);

}
