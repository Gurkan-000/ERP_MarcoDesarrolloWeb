package com.example.erp.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.erp.entities.Pedido;
import com.example.erp.entities.enums.TipoPedido;
import com.example.erp.entities.Mesa;

public interface PedidoRepository extends JpaRepository<Pedido, UUID> {

    public Pedido findByMesa(Mesa mesa);

    public List<Pedido> findByTipoPedido(TipoPedido tipoPedido);

 
    long countByTipoPedido(TipoPedido tipoPedido);

    @Query("""
    SELECT p
    FROM Pedido p
    WHERE MONTH(p.fechaPedido) = :mes
      AND YEAR(p.fechaPedido) = :anio
    """)
    List<Pedido> obtenerPedidosMes(
            @Param("mes") Integer mes,
            @Param("anio") Integer anio
    );
}

