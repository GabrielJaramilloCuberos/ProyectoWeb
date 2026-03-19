package com.example.vigilapp.controllers;

import com.example.vigilapp.entities.Notificacion;
import com.example.vigilapp.exception.NotificacionNotFoundException;
import com.example.vigilapp.repositories.NotificacionRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {

    private final NotificacionRepository notificacionRepository;

    public NotificacionController(NotificacionRepository notificacionRepository) {
        this.notificacionRepository = notificacionRepository;
    }

    @GetMapping
    public ResponseEntity<List<Notificacion>> getAll() {
        List<Notificacion> notificaciones = notificacionRepository.findAll();
        if (notificaciones.isEmpty()) {
            throw new NotificacionNotFoundException("No se encontraron notificaciones");
        }
        return ResponseEntity.status(HttpStatus.OK).body(notificaciones);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Notificacion> getById(@PathVariable Long id) {
        Notificacion notificacion = notificacionRepository.findById(id)
                .orElseThrow(() -> new NotificacionNotFoundException("Notificación no encontrada con id: " + id));
        return ResponseEntity.status(HttpStatus.OK).body(notificacion);
    }

    @PostMapping
    public ResponseEntity<Notificacion> create(@Valid @RequestBody Notificacion notificacion) {
        Notificacion created = notificacionRepository.save(notificacion);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Notificacion> update(@PathVariable Long id, @Valid @RequestBody Notificacion notificacion) {
        Notificacion existing = notificacionRepository.findById(id)
                .orElseThrow(() -> new NotificacionNotFoundException("Notificación no encontrada con id: " + id));
        existing.setMensaje(notificacion.getMensaje());
        existing.setTipo(notificacion.getTipo());
        existing.setFecha_envio(notificacion.getFecha_envio());
        existing.setLeida(notificacion.getLeida());
        existing.setUsuario(notificacion.getUsuario());
        Notificacion updated = notificacionRepository.save(existing);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Notificacion existing = notificacionRepository.findById(id)
                .orElseThrow(() -> new NotificacionNotFoundException("Notificación no encontrada con id: " + id));
        notificacionRepository.delete(existing);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
