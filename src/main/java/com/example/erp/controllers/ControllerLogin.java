package com.example.erp.controllers;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.erp.models.Usuario;

@Controller
@RequestMapping("/api/user")
public class ControllerLogin {

    private List<Usuario> listUsuario = Arrays.asList(new Usuario("admin","admin123"),
                                                        new Usuario("caja","caja123"),
                                                        new Usuario("mesero","mesero123"));

    @GetMapping("/login")
    public String autenticacionVista(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "login";
    }

    @PostMapping("/autenticar")
    public String autenticacionUsuario(@ModelAttribute Usuario usuario) {

        Optional<Usuario> usuarioOptional = listUsuario.stream().filter(u -> usuario.getNombre().equals(u.getNombre()) && usuario.getContraseña().equals(u.getContraseña()))
                .findFirst();

        return usuarioOptional.isPresent() ? "index" : "login";
    }

}
