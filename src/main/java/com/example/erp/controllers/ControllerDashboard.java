package com.example.erp.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/dashboard")
@CrossOrigin("*")
public class ControllerDashboard {
    
    @GetMapping("/vista")
    public String dashboardVista(Model model, HttpSession session) {
      
        
        return "dashboard"; 
    }
}