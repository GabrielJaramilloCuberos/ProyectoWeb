package com.example.vigilapp.controllers;

import com.example.vigilapp.entities.Zona;
import com.example.vigilapp.exception.ZonaNotFoundException;
import com.example.vigilapp.repositories.ZonaRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/zonas")
public class ZonaController {

    private final ZonaRepository zonaRepository;

    public ZonaController(ZonaRepository zonaRepository) {
        this.zonaRepository = zonaRepository;
    }

    @GetMapping
    public ResponseEntity<List<Zona>> getAll() {
        List<Zona> zonas = zonaRepository.findAll();
        if (zonas.isEmpty()) {
            throw new ZonaNotFoundException("No se encontraron zonas");
        }
        return ResponseEntity.status(HttpStatus.OK).body(zonas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Zona> getById(@PathVariable Long id) {
        Zona zona = zonaRepository.findById(id)
                .orElseThrow(() -> new ZonaNotFoundException("Zona no encontrada con id: " + id));
        return ResponseEntity.status(HttpStatus.OK).body(zona);
    }

    @PostMapping
    public ResponseEntity<Zona> create(@Valid @RequestBody Zona zona) {
        Zona created = zonaRepository.save(zona);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Zona> update(@PathVariable Long id, @Valid @RequestBody Zona zona) {
        Zona existing = zonaRepository.findById(id)
                .orElseThrow(() -> new ZonaNotFoundException("Zona no encontrada con id: " + id));
        existing.setNombre(zona.getNombre());
        existing.setDescripcion(zona.getDescripcion());
        existing.setTipo(zona.getTipo());
        existing.setActiva(zona.getActiva());
        Zona updated = zonaRepository.save(existing);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Zona existing = zonaRepository.findById(id)
                .orElseThrow(() -> new ZonaNotFoundException("Zona no encontrada con id: " + id));
        zonaRepository.delete(existing);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}