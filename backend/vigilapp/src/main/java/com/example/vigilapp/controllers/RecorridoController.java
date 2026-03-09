package com.example.vigilapp.controllers;

import com.example.vigilapp.entities.Recorrido;
import com.example.vigilapp.repositories.RecorridoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/recorridos")
public class RecorridoController {

    private final RecorridoRepository recorridoRepository;

    public RecorridoController(RecorridoRepository recorridoRepository) {
        this.recorridoRepository = recorridoRepository;
    }

    @GetMapping
    public List<Recorrido> getAll() {
        return recorridoRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Recorrido> getById(@PathVariable Long id) {
        return recorridoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Recorrido create(@RequestBody Recorrido recorrido) {
        return recorridoRepository.save(recorrido);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Recorrido> update(@PathVariable Long id, @RequestBody Recorrido recorrido) {
        return recorridoRepository.findById(id).map(existing -> {
            existing.setFecha_hora(recorrido.getFecha_hora());
            existing.setTurno(recorrido.getTurno());
            existing.setCheckpoint(recorrido.getCheckpoint());
            return ResponseEntity.ok(recorridoRepository.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return recorridoRepository.findById(id).map(existing -> {
            recorridoRepository.delete(existing);
            return ResponseEntity.ok().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }
}
