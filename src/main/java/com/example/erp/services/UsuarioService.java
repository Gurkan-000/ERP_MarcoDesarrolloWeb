package com.example.erp.services;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.erp.DTOs.request.RequestRegister;
import com.example.erp.DTOs.response.ResponseUsuario;
import com.example.erp.entities.Usuario;
import com.example.erp.entities.enums.Rol;
import com.example.erp.exceptions.AutenticacionException;
import com.example.erp.mappers.MapperUsuario;
import com.example.erp.repositories.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<ResponseUsuario> obtenerUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(MapperUsuario::toDTO)
                .toList();
    }

    // private void cargarUsuariosIniciales() {
    //     usuarios.addAll(Arrays.asList(
    //             new Usuario("Jose Armas", "Jose24", "admin123", Rol.Administrador),
    //             new Usuario("Marco Castro", "Marco20", "caja123", Rol.Cajero),
    //             new Usuario("Miguel Arrairan", "Miguel22", "mesero123", Rol.Mesero)
    //     ));
    // }

    public ResponseUsuario insertarUsuario(RequestRegister requestRegister) {

        Rol rol = Rol.valueOf(requestRegister.getRol().toUpperCase());

        if(rol != Rol.MESERO && rol != Rol.CAJERO){
            throw new AutenticacionException("Rol invalido");
        }

        String usuario = requestRegister.getUsuario();

        if (usuarioRepository.findByUsuario(usuario).isPresent()) {
            throw new AutenticacionException("El nombre de usuario ya existe");
        }

        String contrasenaEncryptada = passwordEncoder.encode(requestRegister.getContrasena());

        Usuario nuevoUsuario = MapperUsuario.toEntity(requestRegister, contrasenaEncryptada, rol.name());

        nuevoUsuario = usuarioRepository.save(nuevoUsuario);

        return MapperUsuario.toDTO(nuevoUsuario);
    }

}
