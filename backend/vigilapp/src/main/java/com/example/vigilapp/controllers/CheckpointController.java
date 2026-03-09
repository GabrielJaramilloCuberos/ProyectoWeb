package com.example.vigilapp.controllers;

import com.example.vigilapp.entities.Checkpoint;
import com.example.vigilapp.repositories.CheckpointRepository;
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
    public List<Checkpoint> getAll() {
        return checkpointRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Checkpoint> getById(@PathVariable Long id) {
        return checkpointRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Checkpoint create(@RequestBody Checkpoint checkpoint) {
        return checkpointRepository.save(checkpoint);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Checkpoint> update(@PathVariable Long id, @RequestBody Checkpoint checkpoint) {
        return checkpointRepository.findById(id).map(existing -> {
            existing.setNombre(checkpoint.getNombre());
            existing.setCodigo_qr(checkpoint.getCodigo_qr());
            existing.setZona(checkpoint.getZona());
            return ResponseEntity.ok(checkpointRepository.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return checkpointRepository.findById(id).map(existing -> {
            checkpointRepository.delete(existing);
            return ResponseEntity.ok().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }
}
