package com.example.vigilapp.controllers;

import com.example.vigilapp.entities.Rol;
import com.example.vigilapp.services.RolService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RolController {

    private final RolService rolService;

    public RolController(RolService rolService) {
        this.rolService = rolService;
    }

    @GetMapping
    public ResponseEntity<List<Rol>> getAll() {
        List<Rol> roles = rolService.getAll();
        return ResponseEntity.status(HttpStatus.OK).body(roles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Rol> getById(@PathVariable Long id) {
        Rol rol = rolService.getById(id);
        return ResponseEntity.status(HttpStatus.OK).body(rol);
    }

    @PostMapping
    public ResponseEntity<Rol> create(@Valid @RequestBody Rol rol) {
        Rol created = rolService.create(rol);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }


    @PutMapping("/{id}")
    public ResponseEntity<Rol> update(@PathVariable Long id, @Valid @RequestBody Rol rol) {
        Rol updated = rolService.update(id, rol);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        rolService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}