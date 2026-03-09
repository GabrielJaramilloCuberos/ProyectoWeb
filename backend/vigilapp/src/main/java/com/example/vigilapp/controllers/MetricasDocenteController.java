package com.example.vigilapp.controllers;

import com.example.vigilapp.entities.MetricasDocente;
import com.example.vigilapp.repositories.MetricasDocenteRepository;
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
    public List<MetricasDocente> getAll() {
        return metricasDocenteRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MetricasDocente> getById(@PathVariable Long id) {
        return metricasDocenteRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public MetricasDocente create(@RequestBody MetricasDocente metricasDocente) {
        return metricasDocenteRepository.save(metricasDocente);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MetricasDocente> update(@PathVariable Long id, @RequestBody MetricasDocente metricasDocente) {
        return metricasDocenteRepository.findById(id).map(existing -> {
            existing.setPeriodo(metricasDocente.getPeriodo());
            existing.setPuntualidad_porcentaje(metricasDocente.getPuntualidad_porcentaje());
            existing.setRecorridos_promedio(metricasDocente.getRecorridos_promedio());
            existing.setIncidentes_reportados(metricasDocente.getIncidentes_reportados());
            existing.setPuntos_totales(metricasDocente.getPuntos_totales());
            existing.setDocente(metricasDocente.getDocente());
            return ResponseEntity.ok(metricasDocenteRepository.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return metricasDocenteRepository.findById(id).map(existing -> {
            metricasDocenteRepository.delete(existing);
            return ResponseEntity.ok().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }
}
