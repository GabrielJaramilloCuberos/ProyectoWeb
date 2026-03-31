package com.example.vigilapp.services;

import com.example.vigilapp.entities.Rol;
import com.example.vigilapp.entities.Usuario;
import com.example.vigilapp.exception.RolNotFoundException;
import com.example.vigilapp.exception.UsuarioNotFoundException;
import com.example.vigilapp.repositories.RolRepository;
import com.example.vigilapp.repositories.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          RolRepository rolRepository,
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Usuario> getAll() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        if (usuarios.isEmpty()) {
            throw new UsuarioNotFoundException("No se encontraron usuarios");
        }
        return usuarios;
    }

    public Usuario getById(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNotFoundException("Usuario no encontrado con id: " + id));
    }

    public Usuario create(Usuario usuario) {
        usuario.setRol(resolveRol(usuario));
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return usuarioRepository.save(usuario);
    }

    public Usuario update(Long id, Usuario usuario) {
        Usuario existing = getById(id);
        existing.setNombre(usuario.getNombre());
        existing.setEmail(usuario.getEmail());
        existing.setEstado(usuario.getEstado());
        existing.setRol(resolveRol(usuario));
        existing.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return usuarioRepository.save(existing);
    }

    public void delete(Long id) {
        Usuario existing = getById(id);
        usuarioRepository.delete(existing);
    }

    private Rol resolveRol(Usuario usuario) {
        if (usuario.getRol() == null || usuario.getRol().getId_rol() == null) {
            throw new IllegalArgumentException("El rol es obligatorio y debe tener un id valido");
        }

        return rolRepository.findById(usuario.getRol().getId_rol())
                .orElseThrow(() -> new RolNotFoundException("Rol no encontrado con id: " + usuario.getRol().getId_rol()));
    }
}
