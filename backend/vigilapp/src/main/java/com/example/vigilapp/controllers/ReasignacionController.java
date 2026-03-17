package com.example.vigilapp.controllers;

import com.example.vigilapp.entities.Reasignacion;
import com.example.vigilapp.exception.ReasignacionNotFoundException;
import com.example.vigilapp.repositories.ReasignacionRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/reasignaciones")
public class ReasignacionController {

    private final ReasignacionRepository reasignacionRepository;

    public ReasignacionController(ReasignacionRepository reasignacionRepository) {
        this.reasignacionRepository = reasignacionRepository;
    }

    @GetMapping
    public ResponseEntity<List<Reasignacion>> getAll() {
        List<Reasignacion> reasignaciones = reasignacionRepository.findAll();
        if (reasignaciones.isEmpty()) {
            throw new ReasignacionNotFoundException("No se encontraron reasignaciones");
        }
        return ResponseEntity.status(HttpStatus.OK).body(reasignaciones);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reasignacion> getById(@PathVariable Long id) {
        Reasignacion reasignacion = reasignacionRepository.findById(id)
                .orElseThrow(() -> new ReasignacionNotFoundException("Reasignación no encontrada con id: " + id));
        return ResponseEntity.status(HttpStatus.OK).body(reasignacion);
    }

    @PostMapping
    public ResponseEntity<Reasignacion> create(@Valid @RequestBody Reasignacion reasignacion) {
        Reasignacion created = reasignacionRepository.save(reasignacion);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reasignacion> update(@PathVariable Long id, @Valid @RequestBody Reasignacion reasignacion) {
        Reasignacion existing = reasignacionRepository.findById(id)
                .orElseThrow(() -> new ReasignacionNotFoundException("Reasignación no encontrada con id: " + id));
        existing.setMotivo(reasignacion.getMotivo());
        existing.setFecha_propuesta(reasignacion.getFecha_propuesta());
        existing.setFecha_respuesta(reasignacion.getFecha_respuesta());
        existing.setEstado(reasignacion.getEstado());
        existing.setTurno(reasignacion.getTurno());
        existing.setDocenteOriginal(reasignacion.getDocenteOriginal());
        existing.setDocentePropuesto(reasignacion.getDocentePropuesto());
        Reasignacion updated = reasignacionRepository.save(existing);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Reasignacion existing = reasignacionRepository.findById(id)
                .orElseThrow(() -> new ReasignacionNotFoundException("Reasignación no encontrada con id: " + id));
        reasignacionRepository.delete(existing);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
