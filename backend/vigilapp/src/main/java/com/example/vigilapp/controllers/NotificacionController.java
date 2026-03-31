package com.example.vigilapp.controllers;

import com.example.vigilapp.entities.Notificacion;
import com.example.vigilapp.services.NotificacionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {

    private final NotificacionService notificacionService;

    public NotificacionController(NotificacionService notificacionService) {
        this.notificacionService = notificacionService;
    }

    @GetMapping
    public ResponseEntity<List<Notificacion>> getAll() {
        List<Notificacion> notificaciones = notificacionService.getAll();
        return ResponseEntity.status(HttpStatus.OK).body(notificaciones);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Notificacion> getById(@PathVariable Long id) {
        Notificacion notificacion = notificacionService.getById(id);
        return ResponseEntity.status(HttpStatus.OK).body(notificacion);
    }

    @PostMapping
    public ResponseEntity<Notificacion> create(@Valid @RequestBody Notificacion notificacion) {
        Notificacion created = notificacionService.create(notificacion);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Notificacion> update(@PathVariable Long id, @Valid @RequestBody Notificacion notificacion) {
        Notificacion updated = notificacionService.update(id, notificacion);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        notificacionService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
