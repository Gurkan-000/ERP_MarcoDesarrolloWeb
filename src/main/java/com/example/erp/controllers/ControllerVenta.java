package com.example.erp.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.erp.DTOs.request.RequestDetallePedido;
import com.example.erp.DTOs.request.RequestPedido;
import com.example.erp.DTOs.response.ResponseDetallePedido;
import com.example.erp.DTOs.response.ResponsePedido;
import com.example.erp.entities.enums.TipoPedido;
import com.example.erp.services.VentaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/venta")
public class ControllerVenta {

    private final VentaService ventaService;

    public ControllerVenta(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @GetMapping("/detallePedidosMesa/{idMesa}")
    public ResponseEntity<List<ResponseDetallePedido>> obtenerDetallesPedidoPorMesa(@PathVariable UUID idMesa) {
        List<ResponseDetallePedido> responseDetallePedidos = ventaService.obtenerDetallesPedidoPorMesa(idMesa);

        return ResponseEntity.ok().body(responseDetallePedidos);
    }

    @GetMapping("/obtenerPedidosPorTipo")
    public ResponseEntity<List<ResponsePedido>> obtenerPedidosPorTipo(@RequestParam(required = true) TipoPedido tipoPedido) {
        List<ResponsePedido> responsePedidos = ventaService.obtenerPedidosPorTipo(tipoPedido);

        return ResponseEntity.ok().body(responsePedidos);
    }

    @PostMapping("/realizarPedido")
    public ResponseEntity<ResponsePedido> realizarPedido(@Valid @RequestBody RequestPedido requestPedido) {

        ResponsePedido responsePedido = ventaService.insertarPedido(requestPedido);

        return ResponseEntity.ok(responsePedido);
    }

    @PostMapping("/agregarDetallePedido/{idPedido}")
    public ResponseEntity<Void> agregarDetallePedido(@PathVariable UUID idPedido, @Valid @RequestBody RequestDetallePedido requestDetallePedido) {

        ventaService.insertarDetallePedido(requestDetallePedido, idPedido);

        return ResponseEntity.noContent().build();
    }

}
