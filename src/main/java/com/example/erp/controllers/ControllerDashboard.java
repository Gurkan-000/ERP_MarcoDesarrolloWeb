package com.example.erp.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.erp.DTOs.response.ResponseDashboard;
import com.example.erp.DTOs.response.ResponsePedidoEspera;
import com.example.erp.DTOs.response.ResponsePedidoMensual;
import com.example.erp.services.DashboardService;

@RestController
@RequestMapping("/api/dashboard")
public class ControllerDashboard {

    private final DashboardService dashboardService;

    public ControllerDashboard(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/estadisticas")
    public ResponseEntity<ResponseDashboard> obtenerEstadisticas() {
        ResponseDashboard estadisticas = dashboardService.obtenerEstadisticas();
        return ResponseEntity.ok(estadisticas);
    }

    @GetMapping("/pedidosMensuales")
    public ResponseEntity<List<ResponsePedidoMensual>> obtenerPedidosMes(
            @RequestParam Integer mes,
            @RequestParam Integer anio) {

        List<ResponsePedidoMensual> pedidosMensuales = dashboardService.obtenerPedidosMes(mes, anio);

        return ResponseEntity.ok(pedidosMensuales);
    }

    @GetMapping("/pedidosEnEspera")
    public ResponseEntity<List<ResponsePedidoEspera>> obtenerPedidosEnEspera() {
        List<ResponsePedidoEspera> pedidosEnEspera = dashboardService.obtenerPedidosEnEspera();
        return ResponseEntity.ok(pedidosEnEspera);
    }

}