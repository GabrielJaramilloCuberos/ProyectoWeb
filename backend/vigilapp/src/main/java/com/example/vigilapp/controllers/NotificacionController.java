package com.example.vigilapp.controllers;

import com.example.vigilapp.entities.Notificacion;
import com.example.vigilapp.repositories.NotificacionRepository;
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
    public List<Notificacion> getAll() {
        return notificacionRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Notificacion> getById(@PathVariable Long id) {
        return notificacionRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Notificacion create(@RequestBody Notificacion notificacion) {
        return notificacionRepository.save(notificacion);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Notificacion> update(@PathVariable Long id, @RequestBody Notificacion notificacion) {
        return notificacionRepository.findById(id).map(existing -> {
            existing.setMensaje(notificacion.getMensaje());
            existing.setTipo(notificacion.getTipo());
            existing.setFecha_envio(notificacion.getFecha_envio());
            existing.setLeida(notificacion.getLeida());
            existing.setUsuario(notificacion.getUsuario());
            return ResponseEntity.ok(notificacionRepository.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return notificacionRepository.findById(id).map(existing -> {
            notificacionRepository.delete(existing);
            return ResponseEntity.ok().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }
}
