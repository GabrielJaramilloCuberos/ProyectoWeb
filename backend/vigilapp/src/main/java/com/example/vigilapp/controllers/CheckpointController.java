package com.example.vigilapp.controllers;

import com.example.vigilapp.entities.Checkpoint;
import com.example.vigilapp.exception.CheckpointNotFoundException;
import com.example.vigilapp.repositories.CheckpointRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/checkpoints")
public class CheckpointController {

    private final CheckpointRepository checkpointRepository;

    public CheckpointController(CheckpointRepository checkpointRepository) {
        this.checkpointRepository = checkpointRepository;
    }

    @GetMapping
    public ResponseEntity<List<Checkpoint>> getAll() {
        List<Checkpoint> checkpoints = checkpointRepository.findAll();
        if (checkpoints.isEmpty()) {
            throw new CheckpointNotFoundException("No se encontraron checkpoints");
        }
        return ResponseEntity.status(HttpStatus.OK).body(checkpoints);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Checkpoint> getById(@PathVariable Long id) {
        Checkpoint checkpoint = checkpointRepository.findById(id)
                .orElseThrow(() -> new CheckpointNotFoundException("Checkpoint no encontrado con id: " + id));
        return ResponseEntity.status(HttpStatus.OK).body(checkpoint);
    }

    @PostMapping
    public ResponseEntity<Checkpoint> create(@Valid @RequestBody Checkpoint checkpoint) {
        Checkpoint created = checkpointRepository.save(checkpoint);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Checkpoint> update(@PathVariable Long id, @Valid @RequestBody Checkpoint checkpoint) {
        Checkpoint existing = checkpointRepository.findById(id)
                .orElseThrow(() -> new CheckpointNotFoundException("Checkpoint no encontrado con id: " + id));
        existing.setNombre(checkpoint.getNombre());
        existing.setCodigo_qr(checkpoint.getCodigo_qr());
        existing.setZona(checkpoint.getZona());
        Checkpoint updated = checkpointRepository.save(existing);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Checkpoint existing = checkpointRepository.findById(id)
                .orElseThrow(() -> new CheckpointNotFoundException("Checkpoint no encontrado con id: " + id));
        checkpointRepository.delete(existing);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
