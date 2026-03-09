package com.example.vigilapp.controllers;

import com.example.vigilapp.entities.Prueba;
import com.example.vigilapp.repositories.PruebaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/pruebas")
public class PruebaController {

    private final PruebaRepository pruebaRepository;

    public PruebaController(PruebaRepository pruebaRepository) {
        this.pruebaRepository = pruebaRepository;
    }

    @GetMapping
    public List<Prueba> getAll() {
        return pruebaRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Prueba> getById(@PathVariable Long id) {
        return pruebaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Prueba create(@RequestBody Prueba prueba) {
        return pruebaRepository.save(prueba);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Prueba> update(@PathVariable Long id, @RequestBody Prueba prueba) {
        return pruebaRepository.findById(id).map(existing -> {
            existing.setMensaje(prueba.getMensaje());
            existing.setNombre(prueba.getNombre());
            return ResponseEntity.ok(pruebaRepository.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return pruebaRepository.findById(id).map(existing -> {
            pruebaRepository.delete(existing);
            return ResponseEntity.ok().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }
}
