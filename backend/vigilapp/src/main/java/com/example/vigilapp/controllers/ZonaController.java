package com.example.vigilapp.controllers;

import com.example.vigilapp.entities.Zona;
import com.example.vigilapp.services.ZonaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/zonas")
public class ZonaController {

    private final ZonaService zonaService;

    public ZonaController(ZonaService zonaService) {
        this.zonaService = zonaService;
    }

    @GetMapping
    public ResponseEntity<List<Zona>> getAll() {
        List<Zona> zonas = zonaService.getAll();
        return ResponseEntity.status(HttpStatus.OK).body(zonas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Zona> getById(@PathVariable Long id) {
        Zona zona = zonaService.getById(id);
        return ResponseEntity.status(HttpStatus.OK).body(zona);
    }

    @PostMapping
    public ResponseEntity<Zona> create(@Valid @RequestBody Zona zona) {
        Zona created = zonaService.create(zona);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Zona> update(@PathVariable Long id, @Valid @RequestBody Zona zona) {
        Zona updated = zonaService.update(id, zona);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        zonaService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}