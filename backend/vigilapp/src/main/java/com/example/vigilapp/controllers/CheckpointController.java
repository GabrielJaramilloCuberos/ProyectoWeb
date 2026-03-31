package com.example.vigilapp.controllers;

import com.example.vigilapp.entities.Checkpoint;
import com.example.vigilapp.services.CheckpointService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/checkpoints")
public class CheckpointController {

    private final CheckpointService checkpointService;

    public CheckpointController(CheckpointService checkpointService) {
        this.checkpointService = checkpointService;
    }

    @GetMapping
    public ResponseEntity<List<Checkpoint>> getAll() {
        List<Checkpoint> checkpoints = checkpointService.getAll();
        return ResponseEntity.status(HttpStatus.OK).body(checkpoints);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Checkpoint> getById(@PathVariable Long id) {
        Checkpoint checkpoint = checkpointService.getById(id);
        return ResponseEntity.status(HttpStatus.OK).body(checkpoint);
    }

    @PostMapping
    public ResponseEntity<Checkpoint> create(@Valid @RequestBody Checkpoint checkpoint) {
        Checkpoint created = checkpointService.create(checkpoint);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Checkpoint> update(@PathVariable Long id, @Valid @RequestBody Checkpoint checkpoint) {
        Checkpoint updated = checkpointService.update(id, checkpoint);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        checkpointService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
