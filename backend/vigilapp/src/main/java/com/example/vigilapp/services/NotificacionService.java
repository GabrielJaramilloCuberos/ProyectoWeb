package com.example.vigilapp.services;

import com.example.vigilapp.entities.Notificacion;
import com.example.vigilapp.exception.NotificacionNotFoundException;
import com.example.vigilapp.repositories.NotificacionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;

    public NotificacionService(NotificacionRepository notificacionRepository) {
        this.notificacionRepository = notificacionRepository;
    }

    public List<Notificacion> getAll() {
        List<Notificacion> notificaciones = notificacionRepository.findAll();
        if (notificaciones.isEmpty()) {
            throw new NotificacionNotFoundException("No se encontraron notificaciones");
        }
        return notificaciones;
    }

    public Notificacion getById(Long id) {
        return notificacionRepository.findById(id)
                .orElseThrow(() -> new NotificacionNotFoundException("Notificacion no encontrada con id: " + id));
    }

    public Notificacion create(Notificacion notificacion) {
        return notificacionRepository.save(notificacion);
    }

    public Notificacion update(Long id, Notificacion notificacion) {
        Notificacion existing = getById(id);
        existing.setMensaje(notificacion.getMensaje());
        existing.setTipo(notificacion.getTipo());
        existing.setFecha_envio(notificacion.getFecha_envio());
        existing.setLeida(notificacion.getLeida());
        existing.setUsuario(notificacion.getUsuario());
        return notificacionRepository.save(existing);
    }

    public void delete(Long id) {
        Notificacion existing = getById(id);
        notificacionRepository.delete(existing);
    }
}
