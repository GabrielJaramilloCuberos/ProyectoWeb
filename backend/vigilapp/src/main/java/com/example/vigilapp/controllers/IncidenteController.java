package com.example.vigilapp.controllers;

import com.example.vigilapp.entities.Incidente;
import com.example.vigilapp.services.IncidenteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/incidentes")
public class IncidenteController {

    private final IncidenteService incidenteService;

    public IncidenteController(IncidenteService incidenteService) {
        this.incidenteService = incidenteService;
    }

    @GetMapping
    public ResponseEntity<List<Incidente>> getAll() {
        List<Incidente> incidentes = incidenteService.getAll();
        return ResponseEntity.status(HttpStatus.OK).body(incidentes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Incidente> getById(@PathVariable Long id) {
        Incidente incidente = incidenteService.getById(id);
        return ResponseEntity.status(HttpStatus.OK).body(incidente);
    }

    @PostMapping
    public ResponseEntity<Incidente> create(@Valid @RequestBody Incidente incidente) {
        Incidente created = incidenteService.create(incidente);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Incidente> update(@PathVariable Long id, @Valid @RequestBody Incidente incidente) {
        Incidente updated = incidenteService.update(id, incidente);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        incidenteService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
