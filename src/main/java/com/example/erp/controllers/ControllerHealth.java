package com.example.erp.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/health")
public class ControllerHealth {

    @GetMapping("/health")
    public String health(){
        return "Server is ok. Server is running fine.";
    }
}