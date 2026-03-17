package com.example.vigilapp.controllers;

import com.example.vigilapp.entities.MetricasDocente;
import com.example.vigilapp.exception.MetricasDocenteNotFoundException;
import com.example.vigilapp.repositories.MetricasDocenteRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/metricas-docente")
public class MetricasDocenteController {

    private final MetricasDocenteRepository metricasDocenteRepository;

    public MetricasDocenteController(MetricasDocenteRepository metricasDocenteRepository) {
        this.metricasDocenteRepository = metricasDocenteRepository;
    }

    @GetMapping
    public ResponseEntity<List<MetricasDocente>> getAll() {
        List<MetricasDocente> metricas = metricasDocenteRepository.findAll();
        if (metricas.isEmpty()) {
            throw new MetricasDocenteNotFoundException("No se encontraron métricas docente");
        }
        return ResponseEntity.status(HttpStatus.OK).body(metricas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MetricasDocente> getById(@PathVariable Long id) {
        MetricasDocente metrica = metricasDocenteRepository.findById(id)
                .orElseThrow(() -> new MetricasDocenteNotFoundException("Métrica docente no encontrada con id: " + id));
        return ResponseEntity.status(HttpStatus.OK).body(metrica);
    }

    @PostMapping
    public ResponseEntity<MetricasDocente> create(@Valid @RequestBody MetricasDocente metricasDocente) {
        MetricasDocente created = metricasDocenteRepository.save(metricasDocente);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MetricasDocente> update(@PathVariable Long id, @Valid @RequestBody MetricasDocente metricasDocente) {
        MetricasDocente existing = metricasDocenteRepository.findById(id)
                .orElseThrow(() -> new MetricasDocenteNotFoundException("Métrica docente no encontrada con id: " + id));
        existing.setPeriodo(metricasDocente.getPeriodo());
        existing.setPuntualidad_porcentaje(metricasDocente.getPuntualidad_porcentaje());
        existing.setRecorridos_promedio(metricasDocente.getRecorridos_promedio());
        existing.setIncidentes_reportados(metricasDocente.getIncidentes_reportados());
        existing.setPuntos_totales(metricasDocente.getPuntos_totales());
        existing.setDocente(metricasDocente.getDocente());
        MetricasDocente updated = metricasDocenteRepository.save(existing);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        MetricasDocente existing = metricasDocenteRepository.findById(id)
                .orElseThrow(() -> new MetricasDocenteNotFoundException("Métrica docente no encontrada con id: " + id));
        metricasDocenteRepository.delete(existing);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
