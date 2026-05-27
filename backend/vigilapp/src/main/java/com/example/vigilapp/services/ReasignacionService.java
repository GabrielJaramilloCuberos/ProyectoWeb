package com.example.vigilapp.services;

import com.example.vigilapp.entities.Reasignacion;
import com.example.vigilapp.entities.Turno;
import com.example.vigilapp.exception.ReasignacionNotFoundException;
import com.example.vigilapp.exception.TurnoNotFoundException;
import com.example.vigilapp.repositories.ReasignacionRepository;
import com.example.vigilapp.repositories.TurnoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReasignacionService {

    private final ReasignacionRepository reasignacionRepository;
    private final TurnoRepository turnoRepository;

    public ReasignacionService(ReasignacionRepository reasignacionRepository, TurnoRepository turnoRepository) {
        this.reasignacionRepository = reasignacionRepository;
        this.turnoRepository = turnoRepository;
    }

    public List<Reasignacion> getAll() {
        return reasignacionRepository.findAll();
    }

    public Reasignacion getById(Long id) {
        return reasignacionRepository.findById(id)
                .orElseThrow(() -> new ReasignacionNotFoundException("Reasignacion no encontrada con id: " + id));
    }

    @Transactional
    public Reasignacion create(Reasignacion reasignacion) {
        Reasignacion saved = reasignacionRepository.save(reasignacion);

        if ("APROBADA".equalsIgnoreCase(saved.getEstado())) {
            Long idTurno = saved.getTurno().getId_turno();
            Turno turno = turnoRepository.findById(idTurno)
                    .orElseThrow(() -> new TurnoNotFoundException("Turno no encontrado con id: " + idTurno));
            turno.setDocente(saved.getDocentePropuesto());
            turno.setEstado("ASIGNADO");
            turnoRepository.save(turno);
        }

        return saved;
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
