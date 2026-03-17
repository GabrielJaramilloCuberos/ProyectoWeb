package com.example.vigilapp.controllers;

import com.example.vigilapp.entities.Turno;
import com.example.vigilapp.exception.TurnoNotFoundException;
import com.example.vigilapp.repositories.TurnoRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/turnos")
public class TurnoController {

    private final TurnoRepository turnoRepository;

    public TurnoController(TurnoRepository turnoRepository) {
        this.turnoRepository = turnoRepository;
    }

    @GetMapping
    public ResponseEntity<List<Turno>> getAll() {
        List<Turno> turnos = turnoRepository.findAll();
        if (turnos.isEmpty()) {
            throw new TurnoNotFoundException("No se encontraron turnos");
        }
        return ResponseEntity.status(HttpStatus.OK).body(turnos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Turno> getById(@PathVariable Long id) {
        Turno turno = turnoRepository.findById(id)
                .orElseThrow(() -> new TurnoNotFoundException("Turno no encontrado con id: " + id));
        return ResponseEntity.status(HttpStatus.OK).body(turno);
    }

    @PostMapping
    public ResponseEntity<Turno> create(@Valid @RequestBody Turno turno) {
        Turno created = turnoRepository.save(turno);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Turno> update(@PathVariable Long id, @Valid @RequestBody Turno turno) {
        Turno existing = turnoRepository.findById(id)
                .orElseThrow(() -> new TurnoNotFoundException("Turno no encontrado con id: " + id));
        existing.setFecha(turno.getFecha());
        existing.setHora_inicio(turno.getHora_inicio());
        existing.setHora_fin(turno.getHora_fin());
        existing.setEstado(turno.getEstado());
        existing.setLimpieza_calificacion(turno.getLimpieza_calificacion());
        existing.setDocente(turno.getDocente());
        existing.setZona(turno.getZona());
        Turno updated = turnoRepository.save(existing);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Turno existing = turnoRepository.findById(id)
                .orElseThrow(() -> new TurnoNotFoundException("Turno no encontrado con id: " + id));
        turnoRepository.delete(existing);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}