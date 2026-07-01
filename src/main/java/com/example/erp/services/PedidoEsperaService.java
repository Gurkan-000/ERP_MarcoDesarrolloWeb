package com.example.erp.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.erp.DTOs.request.RequestDetallePedido;
import com.example.erp.DTOs.request.RequestPedidoEspera;
import com.example.erp.DTOs.response.ResponsePedidoEspera;
import com.example.erp.entities.DetallePedidoEspera;
import com.example.erp.entities.PedidoEspera;
import com.example.erp.entities.Producto;
import com.example.erp.entities.Usuario;
import com.example.erp.exceptions.EntidadNoEncontradaException;
import com.example.erp.exceptions.ReglaDeNegocioException;
import com.example.erp.mappers.MapperPedidoEspera;
import com.example.erp.repositories.PedidoEsperaRepository;
import com.example.erp.repositories.ProductoRepository;

@Service
public class PedidoEsperaService {

    private final PedidoEsperaRepository pedidoEsperaRepository;
    private final ProductoRepository productoRepository;

    public PedidoEsperaService(PedidoEsperaRepository pedidoEsperaRepository,
            ProductoRepository productoRepository) {
        this.pedidoEsperaRepository = pedidoEsperaRepository;
        this.productoRepository = productoRepository;
    }

    /**
     * Obtiene al usuario autenticado en el contexto de seguridad actual.
     * Gracias a Spring Security, cualquier usuario con sesion activa
     * (independientemente de su rol o dispositivo) puede llamar a este servicio.
     */
    private Usuario obtenerUsuarioAutenticado() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof Usuario usuario) {
            return usuario;
        }

        return null;
    }

    @Transactional(readOnly = true)
    public List<ResponsePedidoEspera> listarPedidosEnEspera() {
        return pedidoEsperaRepository.findAllByOrderByFechaHoraAsc().stream()
                .map(MapperPedidoEspera::toDTO)
                .toList();
    }

    @Transactional
    public ResponsePedidoEspera crearPedidoEnEspera(RequestPedidoEspera requestPedidoEspera) {

        PedidoEspera pedidoEspera = MapperPedidoEspera.toEntity(requestPedidoEspera, obtenerUsuarioAutenticado());

        pedidoEspera.setFechaHora(LocalDateTime.now());

        agregarDetalles(pedidoEspera, requestPedidoEspera.getDetalles());

        pedidoEspera.calcularTotal();

        pedidoEspera = pedidoEsperaRepository.save(pedidoEspera);

        return MapperPedidoEspera.toDTO(pedidoEspera);
    }

    @Transactional
    public ResponsePedidoEspera actualizarPedidoEnEspera(UUID idPedidoEspera, RequestPedidoEspera requestPedidoEspera) {

        PedidoEspera pedidoEspera = pedidoEsperaRepository.findById(idPedidoEspera)
                .orElseThrow(() -> new EntidadNoEncontradaException("Pedido en espera no encontrado"));

        pedidoEspera.setTipoPedido(requestPedidoEspera.getTipoPedido());
        pedidoEspera.getDetalles().clear();

        agregarDetalles(pedidoEspera, requestPedidoEspera.getDetalles());

        pedidoEspera.calcularTotal();

        return MapperPedidoEspera.toDTO(pedidoEspera);
    }

    @Transactional
    public void eliminarPedidoEnEspera(UUID idPedidoEspera) {

        if (!pedidoEsperaRepository.existsById(idPedidoEspera)) {
            throw new EntidadNoEncontradaException("Pedido en espera no encontrado");
        }

        pedidoEsperaRepository.deleteById(idPedidoEspera);
    }

    private void agregarDetalles(PedidoEspera pedidoEspera, List<RequestDetallePedido> detallesRequest) {

        for (RequestDetallePedido requestDetalle : detallesRequest) {

            Producto producto = productoRepository.findById(requestDetalle.getIdProducto())
                    .orElseThrow(() -> new EntidadNoEncontradaException("Producto no encontrado"));

            if (requestDetalle.getCantidad() > producto.getStock()) {
                throw new ReglaDeNegocioException("Stock insuficiente");
            }

            DetallePedidoEspera detalle = new DetallePedidoEspera();

            detalle.setCantidad(requestDetalle.getCantidad());
            detalle.setProducto(producto);
            detalle.setPrecioUnitario(producto.getPrecio());
            detalle.calcularTotal();

            pedidoEspera.addDetalle(detalle);
        }
    }

}
