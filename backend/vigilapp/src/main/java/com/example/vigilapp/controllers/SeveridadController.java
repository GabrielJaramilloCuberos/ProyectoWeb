package com.example.vigilapp.controllers;

import com.example.vigilapp.entities.Severidad;
import com.example.vigilapp.exception.SeveridadNotFoundException;
import com.example.vigilapp.repositories.SeveridadRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/severidades")
public class SeveridadController {

    private final SeveridadRepository severidadRepository;

    public SeveridadController(SeveridadRepository severidadRepository) {
        this.severidadRepository = severidadRepository;
    }

    @GetMapping
    public ResponseEntity<List<Severidad>> getAll() {
        List<Severidad> severidades = severidadRepository.findAll();
        if (severidades.isEmpty()) {
            throw new SeveridadNotFoundException("No se encontraron severidades");
        }
        return ResponseEntity.status(HttpStatus.OK).body(severidades);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Severidad> getById(@PathVariable Long id) {
        Severidad severidad = severidadRepository.findById(id)
                .orElseThrow(() -> new SeveridadNotFoundException("Severidad no encontrada con id: " + id));
        return ResponseEntity.status(HttpStatus.OK).body(severidad);
    }

    @PostMapping
    public ResponseEntity<Severidad> create(@Valid @RequestBody Severidad severidad) {
        Severidad created = severidadRepository.save(severidad);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Severidad> update(@PathVariable Long id, @Valid @RequestBody Severidad severidad) {
        Severidad existing = severidadRepository.findById(id)
                .orElseThrow(() -> new SeveridadNotFoundException("Severidad no encontrada con id: " + id));
        existing.setCodigo(severidad.getCodigo());
        existing.setDescripcion(severidad.getDescripcion());
        Severidad updated = severidadRepository.save(existing);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Severidad existing = severidadRepository.findById(id)
                .orElseThrow(() -> new SeveridadNotFoundException("Severidad no encontrada con id: " + id));
        severidadRepository.delete(existing);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
