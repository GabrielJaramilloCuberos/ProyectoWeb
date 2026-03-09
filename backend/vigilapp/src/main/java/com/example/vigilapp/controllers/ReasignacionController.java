package com.example.vigilapp.controllers;

import com.example.vigilapp.entities.Reasignacion;
import com.example.vigilapp.repositories.ReasignacionRepository;
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
    public List<Reasignacion> getAll() {
        return reasignacionRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reasignacion> getById(@PathVariable Long id) {
        return reasignacionRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Reasignacion create(@RequestBody Reasignacion reasignacion) {
        return reasignacionRepository.save(reasignacion);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reasignacion> update(@PathVariable Long id, @RequestBody Reasignacion reasignacion) {
        return reasignacionRepository.findById(id).map(existing -> {
            existing.setMotivo(reasignacion.getMotivo());
            existing.setFecha_propuesta(reasignacion.getFecha_propuesta());
            existing.setFecha_respuesta(reasignacion.getFecha_respuesta());
            existing.setEstado(reasignacion.getEstado());
            existing.setTurno(reasignacion.getTurno());
            existing.setDocenteOriginal(reasignacion.getDocenteOriginal());
            existing.setDocentePropuesto(reasignacion.getDocentePropuesto());
            return ResponseEntity.ok(reasignacionRepository.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return reasignacionRepository.findById(id).map(existing -> {
            reasignacionRepository.delete(existing);
            return ResponseEntity.ok().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }
}
