package com.example.vigilapp.controllers;

import com.example.vigilapp.entities.CheckinTurno;
import com.example.vigilapp.exception.CheckinTurnoNotFoundException;
import com.example.vigilapp.repositories.CheckinTurnoRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/checkinturno")
public class CheckinTurnoController {

    private final CheckinTurnoRepository checkinTurnoRepository;

    public CheckinTurnoController(CheckinTurnoRepository checkinTurnoRepository) {
        this.checkinTurnoRepository = checkinTurnoRepository;
    }

    @GetMapping
    public ResponseEntity<List<CheckinTurno>> getAll() {
        List<CheckinTurno> checkins = checkinTurnoRepository.findAll();
        if (checkins.isEmpty()) {
            throw new CheckinTurnoNotFoundException("No se encontraron checkins de turno");
        }
        return ResponseEntity.status(HttpStatus.OK).body(checkins);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CheckinTurno> getById(@PathVariable Long id) {
        CheckinTurno checkinTurno = checkinTurnoRepository.findById(id)
                .orElseThrow(() -> new CheckinTurnoNotFoundException("Checkin de turno no encontrado con id: " + id));
        return ResponseEntity.status(HttpStatus.OK).body(checkinTurno);
    }

    @PostMapping
    public ResponseEntity<CheckinTurno> create(@Valid @RequestBody CheckinTurno checkinTurno) {
        CheckinTurno created = checkinTurnoRepository.save(checkinTurno);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CheckinTurno> update(@PathVariable Long id, @Valid @RequestBody CheckinTurno checkinTurno) {
        CheckinTurno existing = checkinTurnoRepository.findById(id)
                .orElseThrow(() -> new CheckinTurnoNotFoundException("Checkin de turno no encontrado con id: " + id));
        existing.setFecha_hora(checkinTurno.getFecha_hora());
        existing.setMetodo(checkinTurno.getMetodo());
        existing.setTurno(checkinTurno.getTurno());
        CheckinTurno updated = checkinTurnoRepository.save(existing);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        CheckinTurno existing = checkinTurnoRepository.findById(id)
                .orElseThrow(() -> new CheckinTurnoNotFoundException("Checkin de turno no encontrado con id: " + id));
        checkinTurnoRepository.delete(existing);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
