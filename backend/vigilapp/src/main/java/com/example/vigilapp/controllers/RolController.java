package com.example.vigilapp.controllers;

import com.example.vigilapp.entities.Rol;
import com.example.vigilapp.exception.RolNotFoundException;
import com.example.vigilapp.repositories.RolRepository;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RolController {

    private final RolRepository rolRepository;

    public RolController(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }

    @GetMapping
    public ResponseEntity<List<Rol>> getAll() {
        List<Rol> roles = rolRepository.findAll();
        if (roles.isEmpty()) {
            throw new RolNotFoundException("No se encontraron roles");
        }
        return ResponseEntity.status(HttpStatus.OK).body(roles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Rol> getById(@PathVariable Long id) {
        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new RolNotFoundException("Rol no encontrado con id: " + id));
        return ResponseEntity.status(HttpStatus.OK).body(rol);
    }

    @PostMapping
    public ResponseEntity<Rol> create(@Valid @RequestBody Rol rol) {
        Rol created = rolRepository.save(rol);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }


    @PutMapping("/{id}")
    public ResponseEntity<Rol> update(@PathVariable Long id, @Valid @RequestBody Rol rol) {
        Rol existing = rolRepository.findById(id)
                .orElseThrow(() -> new RolNotFoundException("Rol no encontrado con id: " + id));
        existing.setNombre(rol.getNombre());
        Rol updated = rolRepository.save(existing);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Rol existing = rolRepository.findById(id)
                .orElseThrow(() -> new RolNotFoundException("Rol no encontrado con id: " + id));
        rolRepository.delete(existing);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}