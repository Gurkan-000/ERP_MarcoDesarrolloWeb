package com.example.erp.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.erp.DTOs.response.ResponseDashboard;
import com.example.erp.services.DashboardService;

@RestController
@RequestMapping("/dashboard-api")
@CrossOrigin("*")
public class DashboardRestController {

    private final DashboardService dashboardService;

    public DashboardRestController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/estadisticas")
    public ResponseEntity<ResponseDashboard> obtenerEstadisticas() {
        ResponseDashboard estadisticas = dashboardService.obtenerEstadisticas();
        return ResponseEntity.ok(estadisticas);
    }
}