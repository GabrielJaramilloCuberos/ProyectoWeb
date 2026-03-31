package com.example.vigilapp.controllers;

import com.example.vigilapp.entities.Severidad;
import com.example.vigilapp.services.SeveridadService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/severidades")
public class SeveridadController {

    private final SeveridadService severidadService;

    public SeveridadController(SeveridadService severidadService) {
        this.severidadService = severidadService;
    }

    @GetMapping
    public ResponseEntity<List<Severidad>> getAll() {
        List<Severidad> severidades = severidadService.getAll();
        return ResponseEntity.status(HttpStatus.OK).body(severidades);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Severidad> getById(@PathVariable Long id) {
        Severidad severidad = severidadService.getById(id);
        return ResponseEntity.status(HttpStatus.OK).body(severidad);
    }

    @PostMapping
    public ResponseEntity<Severidad> create(@Valid @RequestBody Severidad severidad) {
        Severidad created = severidadService.create(severidad);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Severidad> update(@PathVariable Long id, @Valid @RequestBody Severidad severidad) {
        Severidad updated = severidadService.update(id, severidad);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        severidadService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
