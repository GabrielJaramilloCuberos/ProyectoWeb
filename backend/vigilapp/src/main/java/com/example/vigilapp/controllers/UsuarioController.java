package com.example.vigilapp.controllers;

import com.example.vigilapp.dto.LoginRequest;
import com.example.vigilapp.entities.Rol;
import com.example.vigilapp.entities.Usuario;
import com.example.vigilapp.exception.RolNotFoundException;
import com.example.vigilapp.exception.UsuarioNotFoundException;
import com.example.vigilapp.repositories.RolRepository;
import com.example.vigilapp.repositories.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;

    public UsuarioController(UsuarioRepository usuarioRepository, RolRepository rolRepository) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> getAll() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        if (usuarios.isEmpty()) {
            throw new UsuarioNotFoundException("No se encontraron usuarios");
        }
        return ResponseEntity.status(HttpStatus.OK).body(usuarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> getById(@PathVariable Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNotFoundException("Usuario no encontrado con id: " + id));
        return ResponseEntity.status(HttpStatus.OK).body(usuario);
    }

    @PostMapping("/login")
    public ResponseEntity<Usuario> login(@RequestBody LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsuarioNotFoundException("Credenciales inválidas"));

        if (!usuario.getPassword().equals(request.getPassword())) {
            throw new UsuarioNotFoundException("Credenciales inválidas");
        }

        if (Boolean.FALSE.equals(usuario.getEstado())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(usuario);
    }

    @PostMapping
    public ResponseEntity<Usuario> create(@Valid @RequestBody Usuario usuario) {
        if (usuario.getRol() != null && usuario.getRol().getId_rol() != null) {
            Rol rolExistente = rolRepository.findById(usuario.getRol().getId_rol())
                    .orElseThrow(
                            () -> new RolNotFoundException(
                                    "Rol no encontrado con id: " + usuario.getRol().getId_rol()));
            usuario.setRol(rolExistente);
        } else {
            throw new IllegalArgumentException("El Rol es obligatorio y debe tener un ID válido");
        }
        Usuario created = usuarioRepository.save(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> update(@PathVariable Long id, @Valid @RequestBody Usuario usuario) {
        Usuario existing = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNotFoundException("Usuario no encontrado con id: " + id));
        existing.setNombre(usuario.getNombre());
        existing.setEmail(usuario.getEmail());
        existing.setPassword(usuario.getPassword());
        existing.setEstado(usuario.getEstado());
        existing.setRol(usuario.getRol());
        Usuario updated = usuarioRepository.save(existing);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Usuario existing = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNotFoundException("Usuario no encontrado con id: " + id));
        usuarioRepository.delete(existing);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}