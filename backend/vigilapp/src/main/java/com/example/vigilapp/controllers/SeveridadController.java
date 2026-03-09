package com.example.vigilapp.controllers;

import com.example.vigilapp.entities.Severidad;
import com.example.vigilapp.repositories.SeveridadRepository;
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
    public List<Severidad> getAll() {
        return severidadRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Severidad> getById(@PathVariable Long id) {
        return severidadRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Severidad create(@RequestBody Severidad severidad) {
        return severidadRepository.save(severidad);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Severidad> update(@PathVariable Long id, @RequestBody Severidad severidad) {
        return severidadRepository.findById(id).map(existing -> {
            existing.setCodigo(severidad.getCodigo());
            existing.setDescripcion(severidad.getDescripcion());
            return ResponseEntity.ok(severidadRepository.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return severidadRepository.findById(id).map(existing -> {
            severidadRepository.delete(existing);
            return ResponseEntity.ok().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }
}
