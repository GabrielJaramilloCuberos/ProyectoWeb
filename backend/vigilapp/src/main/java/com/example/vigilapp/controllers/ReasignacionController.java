package com.example.vigilapp.controllers;

import com.example.vigilapp.entities.Reasignacion;
import com.example.vigilapp.services.ReasignacionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/reasignaciones")
public class ReasignacionController {

    private final ReasignacionService reasignacionService;

    public ReasignacionController(ReasignacionService reasignacionService) {
        this.reasignacionService = reasignacionService;
    }

    @GetMapping
    public ResponseEntity<List<Reasignacion>> getAll() {
        List<Reasignacion> reasignaciones = reasignacionService.getAll();
        return ResponseEntity.status(HttpStatus.OK).body(reasignaciones);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reasignacion> getById(@PathVariable Long id) {
        Reasignacion reasignacion = reasignacionService.getById(id);
        return ResponseEntity.status(HttpStatus.OK).body(reasignacion);
    }

    @PostMapping
    public ResponseEntity<Reasignacion> create(@Valid @RequestBody Reasignacion reasignacion) {
        Reasignacion created = reasignacionService.create(reasignacion);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reasignacion> update(@PathVariable Long id, @Valid @RequestBody Reasignacion reasignacion) {
        Reasignacion updated = reasignacionService.update(id, reasignacion);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reasignacionService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
