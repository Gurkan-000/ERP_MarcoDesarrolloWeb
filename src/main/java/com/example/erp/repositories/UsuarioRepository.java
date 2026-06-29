package com.example.erp.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.erp.entities.Usuario;
import com.example.erp.entities.enums.Rol;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

    Optional<Usuario> findByUsuario(String username);
    
    boolean existsByRol(Rol rol);

}
