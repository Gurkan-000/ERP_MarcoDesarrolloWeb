package com.example.erp.entities;

import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import com.example.erp.entities.enums.EstadoMesa;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

@Entity
@Table(
    name="Mesas"
)
public class Mesa {

    @Id
    @UuidGenerator
    private UUID idMesa;

    private Integer numero;

    @Enumerated(EnumType.STRING)
    private EstadoMesa estado;

    @OneToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="idPedido")
    private Pedido pedido;

    public void asignarPedidoAlaMesa(Pedido pedido){
        pedido.setMesa(this);
        this.pedido = pedido;
    }

}
