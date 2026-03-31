package com.example.vigilapp.services;

import com.example.vigilapp.entities.Reasignacion;
import com.example.vigilapp.exception.ReasignacionNotFoundException;
import com.example.vigilapp.repositories.ReasignacionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReasignacionService {

    private final ReasignacionRepository reasignacionRepository;

    public ReasignacionService(ReasignacionRepository reasignacionRepository) {
        this.reasignacionRepository = reasignacionRepository;
    }

    public List<Reasignacion> getAll() {
        List<Reasignacion> reasignaciones = reasignacionRepository.findAll();
        if (reasignaciones.isEmpty()) {
            throw new ReasignacionNotFoundException("No se encontraron reasignaciones");
        }
        return reasignaciones;
    }

    public Reasignacion getById(Long id) {
        return reasignacionRepository.findById(id)
                .orElseThrow(() -> new ReasignacionNotFoundException("Reasignacion no encontrada con id: " + id));
    }

    public Reasignacion create(Reasignacion reasignacion) {
        return reasignacionRepository.save(reasignacion);
    }

    public Reasignacion update(Long id, Reasignacion reasignacion) {
        Reasignacion existing = getById(id);
        existing.setMotivo(reasignacion.getMotivo());
        existing.setFecha_propuesta(reasignacion.getFecha_propuesta());
        existing.setFecha_respuesta(reasignacion.getFecha_respuesta());
        existing.setEstado(reasignacion.getEstado());
        existing.setTurno(reasignacion.getTurno());
        existing.setDocenteOriginal(reasignacion.getDocenteOriginal());
        existing.setDocentePropuesto(reasignacion.getDocentePropuesto());
        return reasignacionRepository.save(existing);
    }

    public void delete(Long id) {
        Reasignacion existing = getById(id);
        reasignacionRepository.delete(existing);
    }
}
