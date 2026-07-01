package com.example.erp.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.erp.entities.PedidoEspera;

public interface PedidoEsperaRepository extends JpaRepository<PedidoEspera, UUID> {

    List<PedidoEspera> findAllByOrderByFechaHoraAsc();

}
