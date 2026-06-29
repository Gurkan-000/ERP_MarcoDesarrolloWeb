package com.example.erp.services;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.erp.DTOs.request.RequestLogin;
import com.example.erp.DTOs.request.RequestRegister;
import com.example.erp.DTOs.response.ResponseAuth;
import com.example.erp.entities.Usuario;
import com.example.erp.entities.enums.Rol;
import com.example.erp.exceptions.AutenticacionException;
import com.example.erp.mappers.MapperUsuario;
import com.example.erp.repositories.UsuarioRepository;
import com.example.erp.security.JwtService;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;
    private final TokenBlackListService blacklistService;

    public AuthService(UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            TokenBlackListService blacklistService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.blacklistService = blacklistService;
    }

    @Transactional
    public ResponseAuth registrarAdmin(RequestRegister request) {

        Rol rol = Rol.valueOf(request.getRol().toUpperCase());
        
        if (rol != Rol.ADMIN) {
            throw new AutenticacionException("Solo se registra con rol de administrador");
        }

        String usuario = request.getUsuario();

        if (usuarioRepository.findByUsuario(usuario).isPresent()) {
            throw new AutenticacionException("El nombre se usuario ya existe");
        }

        if (usuarioRepository.existsByRol(rol)) {
            throw new AutenticacionException("Ya existe un administrador registrado");
        }

        String passwordEncriptada = passwordEncoder.encode(request.getContrasena());

        Usuario nuevoUsuario = MapperUsuario.toEntity(request, passwordEncriptada, rol.name());
        usuarioRepository.save(nuevoUsuario);

        String token = jwtService.generateToken(nuevoUsuario);
        return new ResponseAuth(token);

    }

    @Transactional
    public ResponseAuth iniciarSesion(RequestLogin request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsuario(),
                        request.getContrasena()
                )
        );

        Usuario usuario = usuarioRepository.findByUsuario(request.getUsuario()).get();

        String token = jwtService.generateToken(usuario);
        
        return new ResponseAuth(token);

    }

    @Transactional
    public void cerrarSesion(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Token inválido.");
        }

        String jwt = authHeader.substring(7);

        blacklistService.revocarToken(jwt);
    }

}
