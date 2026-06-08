package com.example.erp.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.erp.DTOs.request.RequestDetallePedido;
import com.example.erp.DTOs.request.RequestMetodoPago;
import com.example.erp.DTOs.request.RequestMovimientoCaja;
import com.example.erp.DTOs.request.RequestPedido;
import com.example.erp.DTOs.response.ResponseDetallePedido;
import com.example.erp.DTOs.response.ResponseMesa;
import com.example.erp.DTOs.response.ResponsePedido;
import com.example.erp.entities.Caja;
import com.example.erp.entities.DetallePedido;
import com.example.erp.entities.Mesa;
import com.example.erp.entities.Pedido;
import com.example.erp.entities.Producto;
import com.example.erp.entities.enums.EstadoCaja;
import com.example.erp.entities.enums.EstadoMesa;
import com.example.erp.entities.enums.Tipo;
import com.example.erp.entities.enums.TipoPedido;
import com.example.erp.exceptions.EntidadNoEncontradaException;
import com.example.erp.exceptions.ReglaDeNegocioException;
import com.example.erp.mappers.MapperDetallePedido;
import com.example.erp.mappers.MapperMesa;
import com.example.erp.mappers.MapperPedido;
import com.example.erp.repositories.CajaRepository;
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
    private final CajaRepository cajaRepository;

    private final CajaService cajaService;

    public VentaService(MesaRepository mesaRepository, PedidoRepository pedidoRepository,
            DetallePedidoRepository detallePedidoRepository, ProductoRepository productoRepository, CajaService cajaService, CajaRepository cajaRepository) {

        this.mesaRepository = mesaRepository;
        this.pedidoRepository = pedidoRepository;
        this.detallePedidoRepository = detallePedidoRepository;
        this.productoRepository = productoRepository;
        this.cajaService = cajaService;
        this.cajaRepository = cajaRepository;

        for (int i = 1; i <= 6; i++) {
            mesaRepository.save(Mesa.builder().estado(EstadoMesa.LIBRE).numero(i).build());
        }
    }

    public List<ResponsePedido> obtenerPedidos() {
        return pedidoRepository.findAll().stream()
                .map(MapperPedido::toDTO)
                .toList();
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

        Pedido pedido = mesa.getPedido();

        List<ResponseDetallePedido> responseDetallePedidos = detallePedidoRepository.findByPedido(pedido).stream()
                .map(MapperDetallePedido::toDTO)
                .toList();

        return responseDetallePedidos;
    }

    @Transactional(readOnly = true)
    public List<ResponseMesa> obtenerMesas() {
        return mesaRepository.findAllByOrderByNumeroAsc().stream()
                .map(MapperMesa::toDTO)
                .toList();
    }

    @Transactional
    public String cobrarPedido(RequestPedido requestPedido) {

        Caja caja = cajaRepository.findByEstado(EstadoCaja.ABIERTO)
                .orElseThrow(() -> new ReglaDeNegocioException("No se puede cobrar con la caja cerrada"));

        Pedido pedido = MapperPedido.toEntity(requestPedido);
        pedido = pedidoRepository.save(pedido);

        for (RequestDetallePedido requestDetallePedido : requestPedido.getDetalles()) {

            Producto producto = productoRepository.findById(requestDetallePedido.getIdProducto())
                    .orElseThrow(() -> new EntidadNoEncontradaException("Producto no encontrado"));

            if (requestDetallePedido.getCantidad() > producto.getStock()) {
                throw new ReglaDeNegocioException("Stock insuficiente");
            }

            producto.setStock(producto.getStock() - requestDetallePedido.getCantidad());

            DetallePedido detallePedido = new DetallePedido();

            detallePedido.setCantidad(requestDetallePedido.getCantidad());
            detallePedido.setProducto(producto);
            detallePedido.setPrecioUnitario(producto.getPrecio());
            detallePedido.calcularTotal();

            pedido.addDetallePedido(detallePedido);

            detallePedidoRepository.save(detallePedido);
        }

        pedido.calcularTotal();

        String concepto;

        if (pedido.getMesa() != null && pedido.getTipoPedido() == TipoPedido.LOCAL) {
            concepto = "Pedido de la mesa " + pedido.getMesa().getNumero();
        } else {
            concepto = "Pedido para " + pedido.getTipoPedido();
        }

        RequestMovimientoCaja requestMovimientoCaja = RequestMovimientoCaja.builder()
                .concepto(concepto)
                .metodo(pedido.getMetodoPago())
                .tipo(Tipo.INGRESO)
                .monto(pedido.getTotal())
                .build();

        cajaService.registrarMovimientoPedido(requestMovimientoCaja, caja.getIdCaja());

        return "Cobrado Exitosamente";
    }

    @Transactional(readOnly = true)
    public void validarDetallePedido(RequestDetallePedido requestDetalle) {

        Producto producto = productoRepository.findById(requestDetalle.getIdProducto())
                .orElseThrow(() -> new EntidadNoEncontradaException("Producto no encontrado"));

        if (requestDetalle.getCantidad() > producto.getStock()) {
            throw new ReglaDeNegocioException("Stock insuficiente");
        }

    }

    @Transactional
    public void ocuparMesa(UUID idMesa, RequestPedido requestPedido) {

        Mesa mesa = mesaRepository.findById(idMesa)
                .orElseThrow(() -> new EntidadNoEncontradaException("Mesa no encontrada"));

        Pedido pedido = MapperPedido.toEntity(requestPedido);
        pedido = pedidoRepository.save(pedido);

        for (RequestDetallePedido requestDetallePedido : requestPedido.getDetalles()) {

            Producto producto = productoRepository.findById(requestDetallePedido.getIdProducto())
                    .orElseThrow(() -> new EntidadNoEncontradaException("Producto no encontrado"));

            if (requestDetallePedido.getCantidad() > producto.getStock()) {
                throw new ReglaDeNegocioException("Stock insuficiente");
            }

            producto.setStock(producto.getStock() - requestDetallePedido.getCantidad());

            DetallePedido detallePedido = new DetallePedido();

            detallePedido.setCantidad(requestDetallePedido.getCantidad());
            detallePedido.setProducto(producto);
            detallePedido.setPrecioUnitario(producto.getPrecio());
            detallePedido.calcularTotal();

            pedido.addDetallePedido(detallePedido);

            detallePedidoRepository.save(detallePedido);
        }

        pedido.calcularTotal();

        mesa.setEstado(EstadoMesa.OCUPADO);
        mesa.asignarPedidoAlaMesa(pedido);

    }

    @Transactional
    public String cobrarMesa(UUID idMesa, RequestMetodoPago requestMetodoPago) {

        Caja caja = cajaRepository.findByEstado(EstadoCaja.ABIERTO)
                .orElseThrow(() -> new ReglaDeNegocioException("No se puede cobrar con la caja cerrada"));

        Mesa mesa = mesaRepository.findById(idMesa)
                .orElseThrow(() -> new EntidadNoEncontradaException("Mesa no encontrada"));

        Pedido pedido = mesa.getPedido();

        if (pedido == null) {
            throw new EntidadNoEncontradaException("La mesa no tiene registrado un pedido");
        }

        String concepto = "Pedido de la mesa " + pedido.getMesa().getNumero();

        RequestMovimientoCaja requestMovimientoCaja = RequestMovimientoCaja.builder()
                .concepto(concepto)
                .metodo(pedido.getMetodoPago())
                .tipo(Tipo.INGRESO)
                .monto(pedido.getTotal())
                .build();

        cajaService.registrarMovimientoPedido(requestMovimientoCaja, caja.getIdCaja());

        mesa.setEstado(EstadoMesa.LIBRE);
        mesa.setPedido(null);

        return "Cobrado Exitosamente";
    }

    @Transactional
    public void agregarDetallePedidoAlaMesa(UUID idMesa, RequestDetallePedido requestDetallePedido) {

        Mesa mesa = mesaRepository.findById(idMesa)
                .orElseThrow(() -> new EntidadNoEncontradaException("Mesa no encontrada"));

        Pedido pedido = mesa.getPedido();

        if (pedido == null) {
            throw new EntidadNoEncontradaException("La mesa no tiene registrado un pedido");
        }

        Producto producto = productoRepository.findById(requestDetallePedido.getIdProducto())
                .orElseThrow(() -> new EntidadNoEncontradaException("Producto no encontrado"));

        if (requestDetallePedido.getCantidad() > producto.getStock()) {
            throw new ReglaDeNegocioException("Stock insuficiente");
        }

        producto.setStock(producto.getStock() - requestDetallePedido.getCantidad());

        Optional<DetallePedido> detalleExistente = pedido.getDetalles().stream()
                .filter(d -> d.getProducto().equals(producto))
                .findFirst();

        if (detalleExistente.isPresent()) {
            detalleExistente.get().setCantidad(detalleExistente.get().getCantidad() + requestDetallePedido.getCantidad());
            detalleExistente.get().calcularTotal();
        } else {
            DetallePedido detallePedido = new DetallePedido();

            detallePedido.setCantidad(requestDetallePedido.getCantidad());
            detallePedido.setProducto(producto);
            detallePedido.setPrecioUnitario(producto.getPrecio());
            detallePedido.calcularTotal();

            detallePedidoRepository.save(detallePedido);

            pedido.addDetallePedido(detallePedido);
        }

        pedido.calcularTotal();

    }

}
