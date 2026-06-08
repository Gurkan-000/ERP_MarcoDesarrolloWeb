package com.example.erp.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.erp.DTOs.request.RequestDetallePedido;
import com.example.erp.DTOs.request.RequestMetodoPago;
import com.example.erp.DTOs.request.RequestPedido;
import com.example.erp.DTOs.response.ResponseDetallePedido;
import com.example.erp.DTOs.response.ResponseMesa;
import com.example.erp.DTOs.response.ResponsePedido;
import com.example.erp.services.VentaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/venta")
@CrossOrigin("*")
public class ControllerVenta {

    private final VentaService ventaService;

    public ControllerVenta(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @GetMapping("/listarPedidos")
    public ResponseEntity<List<ResponsePedido>> obtenerPedidos() {
        List<ResponsePedido> responsePedido = ventaService.obtenerPedidos();

        return ResponseEntity.ok().body(responsePedido);
    }

    @GetMapping("/listarMesas")
    public ResponseEntity<List<ResponseMesa>> obtenerMesas() {
        List<ResponseMesa> responseMesas = ventaService.obtenerMesas();

        return ResponseEntity.ok().body(responseMesas);
    }

    @PostMapping("/cobrarPedido")
    public ResponseEntity<String> cobrarPedido(@Valid @RequestBody RequestPedido requestPedido) {

        String mensaje = ventaService.cobrarPedido(requestPedido);

        return ResponseEntity.ok().body(mensaje);
    }

    @PostMapping("/validarDetallePedido")
    public ResponseEntity<Void> validarDetallePedido(@Valid @RequestBody RequestDetallePedido requestDetallePedido) {

        ventaService.validarDetallePedido(requestDetallePedido);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/detallePedidosPorMesa/{idMesa}")
    public ResponseEntity<List<ResponseDetallePedido>> obtenerDetallesPedidoPorMesa(@PathVariable UUID idMesa) {
        List<ResponseDetallePedido> responseDetallePedidos = ventaService.obtenerDetallesPedidoPorMesa(idMesa);

        return ResponseEntity.ok().body(responseDetallePedidos);
    }

    @PutMapping("/agregarDetallePedidoAlaMesa/{idMesa}")
    public ResponseEntity<Void> agregarDetallePedidoAlaMesa(@PathVariable UUID idMesa, @Valid @RequestBody RequestDetallePedido requestDetallePedido) {

        ventaService.agregarDetallePedidoAlaMesa(idMesa, requestDetallePedido);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/ocuparMesa/{idMesa}")
    public ResponseEntity<Void> ocuparMesa(@PathVariable UUID idMesa, @Valid @RequestBody RequestPedido requestPedido) {

        ventaService.ocuparMesa(idMesa, requestPedido);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/cobrarMesa/{idMesa}")
    public ResponseEntity<String> cobrarMesa(@PathVariable UUID idMesa, @Valid @RequestBody RequestMetodoPago requestMetodoPago){

        String mensaje = ventaService.cobrarMesa(idMesa, requestMetodoPago);
    
        return ResponseEntity.ok().body(mensaje);
    }

}
