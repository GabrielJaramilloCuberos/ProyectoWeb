package com.example.vigilapp.controllers;

import com.example.vigilapp.entities.Recorrido;
import com.example.vigilapp.exception.RecorridoNotFoundException;
import com.example.vigilapp.repositories.RecorridoRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<List<Recorrido>> getAll() {
        List<Recorrido> recorridos = recorridoRepository.findAll();
        if (recorridos.isEmpty()) {
            throw new RecorridoNotFoundException("No se encontraron recorridos");
        }
        return ResponseEntity.status(HttpStatus.OK).body(recorridos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Recorrido> getById(@PathVariable Long id) {
        Recorrido recorrido = recorridoRepository.findById(id)
                .orElseThrow(() -> new RecorridoNotFoundException("Recorrido no encontrado con id: " + id));
        return ResponseEntity.status(HttpStatus.OK).body(recorrido);
    }

    @PostMapping
    public ResponseEntity<Recorrido> create(@Valid @RequestBody Recorrido recorrido) {
        Recorrido created = recorridoRepository.save(recorrido);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Recorrido> update(@PathVariable Long id, @Valid @RequestBody Recorrido recorrido) {
        Recorrido existing = recorridoRepository.findById(id)
                .orElseThrow(() -> new RecorridoNotFoundException("Recorrido no encontrado con id: " + id));
        existing.setFecha_hora(recorrido.getFecha_hora());
        existing.setTurno(recorrido.getTurno());
        existing.setCheckpoint(recorrido.getCheckpoint());
        Recorrido updated = recorridoRepository.save(existing);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Recorrido existing = recorridoRepository.findById(id)
                .orElseThrow(() -> new RecorridoNotFoundException("Recorrido no encontrado con id: " + id));
        recorridoRepository.delete(existing);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
