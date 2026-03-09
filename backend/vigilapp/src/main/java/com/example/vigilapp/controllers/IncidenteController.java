package com.example.vigilapp.controllers;

import com.example.vigilapp.entities.Incidente;
import com.example.vigilapp.repositories.IncidenteRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/incidentes")
public class IncidenteController {

    private final IncidenteRepository incidenteRepository;

    public IncidenteController(IncidenteRepository incidenteRepository) {
        this.incidenteRepository = incidenteRepository;
    }

    @GetMapping
    public List<Incidente> getAll() {
        return incidenteRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Incidente> getById(@PathVariable Long id) {
        return incidenteRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Incidente create(@RequestBody Incidente incidente) {
        return incidenteRepository.save(incidente);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Incidente> update(@PathVariable Long id, @RequestBody Incidente incidente) {
        return incidenteRepository.findById(id).map(existing -> {
            existing.setFecha_hora(incidente.getFecha_hora());
            existing.setDescripcion(incidente.getDescripcion());
            existing.setTurno(incidente.getTurno());
            existing.setZona(incidente.getZona());
            existing.setTipoIncidente(incidente.getTipoIncidente());
            existing.setSeveridad(incidente.getSeveridad());
            return ResponseEntity.ok(incidenteRepository.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return incidenteRepository.findById(id).map(existing -> {
            incidenteRepository.delete(existing);
            return ResponseEntity.ok().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }
}
