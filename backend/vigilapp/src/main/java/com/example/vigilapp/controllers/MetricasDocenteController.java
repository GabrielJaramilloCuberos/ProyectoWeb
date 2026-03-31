package com.example.vigilapp.controllers;

import com.example.vigilapp.entities.MetricasDocente;
import com.example.vigilapp.services.MetricasDocenteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/metricas-docente")
public class MetricasDocenteController {

    private final MetricasDocenteService metricasDocenteService;

    public MetricasDocenteController(MetricasDocenteService metricasDocenteService) {
        this.metricasDocenteService = metricasDocenteService;
    }

    @GetMapping
    public ResponseEntity<List<MetricasDocente>> getAll() {
        List<MetricasDocente> metricas = metricasDocenteService.getAll();
        return ResponseEntity.status(HttpStatus.OK).body(metricas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MetricasDocente> getById(@PathVariable Long id) {
        MetricasDocente metrica = metricasDocenteService.getById(id);
        return ResponseEntity.status(HttpStatus.OK).body(metrica);
    }

    @PostMapping
    public ResponseEntity<MetricasDocente> create(@Valid @RequestBody MetricasDocente metricasDocente) {
        MetricasDocente created = metricasDocenteService.create(metricasDocente);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MetricasDocente> update(@PathVariable Long id, @Valid @RequestBody MetricasDocente metricasDocente) {
        MetricasDocente updated = metricasDocenteService.update(id, metricasDocente);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        metricasDocenteService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
