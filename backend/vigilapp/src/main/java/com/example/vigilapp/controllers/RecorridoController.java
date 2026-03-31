package com.example.vigilapp.controllers;

import com.example.vigilapp.entities.Recorrido;
import com.example.vigilapp.services.RecorridoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/recorridos")
public class RecorridoController {

    private final RecorridoService recorridoService;

    public RecorridoController(RecorridoService recorridoService) {
        this.recorridoService = recorridoService;
    }

    @GetMapping
    public ResponseEntity<List<Recorrido>> getAll() {
        List<Recorrido> recorridos = recorridoService.getAll();
        return ResponseEntity.status(HttpStatus.OK).body(recorridos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Recorrido> getById(@PathVariable Long id) {
        Recorrido recorrido = recorridoService.getById(id);
        return ResponseEntity.status(HttpStatus.OK).body(recorrido);
    }

    @PostMapping
    public ResponseEntity<Recorrido> create(@Valid @RequestBody Recorrido recorrido) {
        Recorrido created = recorridoService.create(recorrido);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Recorrido> update(@PathVariable Long id, @Valid @RequestBody Recorrido recorrido) {
        Recorrido updated = recorridoService.update(id, recorrido);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        recorridoService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
