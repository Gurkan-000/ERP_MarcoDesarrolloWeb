package com.example.erp.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.erp.entities.DetallePedido;
import com.example.erp.entities.Pedido;

public interface DetallePedidoRepository extends  JpaRepository<DetallePedido, UUID>{

    public List<DetallePedido> findByPedido(Pedido pedido); 

}

