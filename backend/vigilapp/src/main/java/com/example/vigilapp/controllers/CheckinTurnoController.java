package com.example.vigilapp.controllers;

import com.example.vigilapp.entities.CheckinTurno;
import com.example.vigilapp.repositories.CheckinTurnoRepository;
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
    public List<CheckinTurno> getAll() {
        return checkinTurnoRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CheckinTurno> getById(@PathVariable Long id) {
        return checkinTurnoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public CheckinTurno create(@RequestBody CheckinTurno checkinTurno) {
        return checkinTurnoRepository.save(checkinTurno);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CheckinTurno> update(@PathVariable Long id, @RequestBody CheckinTurno checkinTurno) {
        return checkinTurnoRepository.findById(id).map(existing -> {
            existing.setFecha_hora(checkinTurno.getFecha_hora());
            existing.setMetodo(checkinTurno.getMetodo());
            existing.setTurno(checkinTurno.getTurno());
            return ResponseEntity.ok(checkinTurnoRepository.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return checkinTurnoRepository.findById(id).map(existing -> {
            checkinTurnoRepository.delete(existing);
            return ResponseEntity.ok().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }
}
