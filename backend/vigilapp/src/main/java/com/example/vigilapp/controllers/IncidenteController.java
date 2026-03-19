package com.example.vigilapp.controllers;

import com.example.vigilapp.entities.Incidente;
import com.example.vigilapp.exception.IncidenteNotFoundException;
import com.example.vigilapp.repositories.IncidenteRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<List<Incidente>> getAll() {
        List<Incidente> incidentes = incidenteRepository.findAll();
        if (incidentes.isEmpty()) {
            throw new IncidenteNotFoundException("No se encontraron incidentes");
        }
        return ResponseEntity.status(HttpStatus.OK).body(incidentes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Incidente> getById(@PathVariable Long id) {
        Incidente incidente = incidenteRepository.findById(id)
                .orElseThrow(() -> new IncidenteNotFoundException("Incidente no encontrado con id: " + id));
        return ResponseEntity.status(HttpStatus.OK).body(incidente);
    }

    @PostMapping
    public ResponseEntity<Incidente> create(@Valid @RequestBody Incidente incidente) {
        Incidente created = incidenteRepository.save(incidente);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Incidente> update(@PathVariable Long id, @Valid @RequestBody Incidente incidente) {
        Incidente existing = incidenteRepository.findById(id)
                .orElseThrow(() -> new IncidenteNotFoundException("Incidente no encontrado con id: " + id));
        existing.setFecha_hora(incidente.getFecha_hora());
        existing.setDescripcion(incidente.getDescripcion());
        existing.setTurno(incidente.getTurno());
        existing.setZona(incidente.getZona());
        existing.setTipoIncidente(incidente.getTipoIncidente());
        existing.setSeveridad(incidente.getSeveridad());
        Incidente updated = incidenteRepository.save(existing);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Incidente existing = incidenteRepository.findById(id)
                .orElseThrow(() -> new IncidenteNotFoundException("Incidente no encontrado con id: " + id));
        incidenteRepository.delete(existing);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
