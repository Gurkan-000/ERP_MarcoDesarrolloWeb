package com.example.erp.services;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.erp.DTOs.request.RequestDetallePedido;
import com.example.erp.DTOs.request.RequestMovimientoCaja;
import com.example.erp.DTOs.request.RequestPedido;
import com.example.erp.DTOs.response.ResponseDetallePedido;
import com.example.erp.DTOs.response.ResponsePedido;
import com.example.erp.entities.DetallePedido;
import com.example.erp.entities.Mesa;
import com.example.erp.entities.Pedido;
import com.example.erp.entities.Producto;
import com.example.erp.entities.enums.EstadoMesa;
import com.example.erp.entities.enums.Tipo;
import com.example.erp.entities.enums.TipoPedido;
import com.example.erp.exceptions.EntidadNoEncontradaException;
import com.example.erp.mappers.MapperDetallePedido;
import com.example.erp.mappers.MapperPedido;
import com.example.erp.repositories.DetallePedidoRepository;
import com.example.erp.repositories.MesaRepository;
import com.example.erp.repositories.PedidoRepository;
import com.example.erp.repositories.ProductoRepository;

@Service
public class VentaService {

    private final MesaRepository mesaRepository;
    private final PedidoRepository pedidoRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final ProductoRepository productoRepository;

    private final CajaService cajaService;

    public VentaService(MesaRepository mesaRepository, PedidoRepository pedidoRepository,
            DetallePedidoRepository detallePedidoRepository, ProductoRepository productoRepository, CajaService cajaService) {
        this.mesaRepository = mesaRepository;
        this.pedidoRepository = pedidoRepository;
        this.detallePedidoRepository = detallePedidoRepository;
        this.productoRepository = productoRepository;
        this.cajaService = cajaService;
    }

    @Transactional(readOnly = true)
    public List<ResponsePedido> obtenerPedidosPorTipo(TipoPedido tipoPedido) {
        return pedidoRepository.findByTipoPedido(tipoPedido).stream()
                .map(MapperPedido::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ResponseDetallePedido> obtenerDetallesPedidoPorMesa(UUID idMesa) {

        Mesa mesa = mesaRepository.findById(idMesa)
                .orElseThrow(() -> new EntidadNoEncontradaException("Mesa no encontrada"));

        Pedido pedido = pedidoRepository.findByMesa(mesa);

        List<ResponseDetallePedido> responseDetallePedidos = detallePedidoRepository.findByPedido(pedido).stream()
                .map(MapperDetallePedido::toDTO)
                .toList();

        return responseDetallePedidos;
    }

    @Transactional
    public ResponsePedido insertarPedido(RequestPedido requestPedido) {

        Pedido pedido = MapperPedido.toEntity(requestPedido);

        if (requestPedido.getIdMesa() != null) {
            Mesa mesa = mesaRepository.findById(requestPedido.getIdMesa())
                    .orElseThrow(() -> new EntidadNoEncontradaException("Mesa no encontrada"));
            mesa.setPedido(pedido);
            mesa.setEstado(EstadoMesa.OCUPADO);
        }

        pedido = pedidoRepository.save(pedido);

        return MapperPedido.toDTO(pedido);
    }

    @Transactional
    public void insertarDetallePedido(RequestDetallePedido requestDetalle, UUID idPedido) {

        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new EntidadNoEncontradaException("Pedido no existente"));

        Producto producto = productoRepository.findById(requestDetalle.getIdProducto())
                .orElseThrow(() -> new EntidadNoEncontradaException("Producto no encontrado"));

        DetallePedido detallePedido = new DetallePedido();

        detallePedido.setProducto(producto);
        detallePedido.setPrecioUnitario(producto.getPrecio());
        detallePedido.setCantidad(requestDetalle.getCantidad());
        detallePedido.calcularTotal();

        pedido.addDetallePedido(detallePedido);
        pedido.calcularTotal();

        detallePedidoRepository.save(detallePedido);
    }

    @Transactional
    public void cobrarPedido(UUID idPedido, UUID idCaja) {

        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new EntidadNoEncontradaException("Pedido no existente"));

        String concepto = (pedido.getMesa() != null) 
                ? "Pedido de la mesa " + pedido.getMesa().getNumero() 
                : "Venta rápida " + pedido.getTipoPedido();

        RequestMovimientoCaja requestMovimientoCaja = RequestMovimientoCaja.builder()
                .concepto(concepto)
                .metodo(pedido.getMetodoPago())
                .tipo(Tipo.INGRESO)
                .monto(pedido.getTotal())
                .build();

        cajaService.registrarMovimientoPedido(requestMovimientoCaja, idCaja);

    }

    @Transactional
    public void ocuparMesa(UUID idMesa) {

        Mesa mesa = mesaRepository.findById(idMesa)
                .orElseThrow(() -> new EntidadNoEncontradaException("Mesa no encontrada"));

        mesa.setEstado(EstadoMesa.OCUPADO);

    }

}
