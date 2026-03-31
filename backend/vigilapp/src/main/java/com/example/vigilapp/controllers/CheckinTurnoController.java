package com.example.vigilapp.controllers;

import com.example.vigilapp.entities.CheckinTurno;
import com.example.vigilapp.services.CheckinTurnoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/checkinturno")
public class CheckinTurnoController {

    private final CheckinTurnoService checkinTurnoService;

    public CheckinTurnoController(CheckinTurnoService checkinTurnoService) {
        this.checkinTurnoService = checkinTurnoService;
    }

    @GetMapping
    public ResponseEntity<List<CheckinTurno>> getAll() {
        List<CheckinTurno> checkins = checkinTurnoService.getAll();
        return ResponseEntity.status(HttpStatus.OK).body(checkins);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CheckinTurno> getById(@PathVariable Long id) {
        CheckinTurno checkinTurno = checkinTurnoService.getById(id);
        return ResponseEntity.status(HttpStatus.OK).body(checkinTurno);
    }

    @PostMapping
    public ResponseEntity<CheckinTurno> create(@Valid @RequestBody CheckinTurno checkinTurno) {
        CheckinTurno created = checkinTurnoService.create(checkinTurno);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CheckinTurno> update(@PathVariable Long id, @Valid @RequestBody CheckinTurno checkinTurno) {
        CheckinTurno updated = checkinTurnoService.update(id, checkinTurno);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        checkinTurnoService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
