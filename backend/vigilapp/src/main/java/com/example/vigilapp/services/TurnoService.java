package com.example.vigilapp.services;

import com.example.vigilapp.entities.Turno;
import com.example.vigilapp.exception.TurnoNotFoundException;
import com.example.vigilapp.repositories.TurnoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TurnoService {

    private final TurnoRepository turnoRepository;

    public TurnoService(TurnoRepository turnoRepository) {
        this.turnoRepository = turnoRepository;
    }

    public List<Turno> getAll() {
        List<Turno> turnos = turnoRepository.findAll();
        if (turnos.isEmpty()) {
            throw new TurnoNotFoundException("No se encontraron turnos");
        }
        return turnos;
    }

    public Turno getById(Long id) {
        return turnoRepository.findById(id)
                .orElseThrow(() -> new TurnoNotFoundException("Turno no encontrado con id: " + id));
    }

    public Turno create(Turno turno) {
        return turnoRepository.save(turno);
    }

    public Turno update(Long id, Turno turno) {
        Turno existing = getById(id);
        existing.setFecha(turno.getFecha());
        existing.setHora_inicio(turno.getHora_inicio());
        existing.setHora_fin(turno.getHora_fin());
        existing.setEstado(turno.getEstado());
        existing.setLimpieza_calificacion(turno.getLimpieza_calificacion());
        existing.setDocente(turno.getDocente());
        existing.setZona(turno.getZona());
        return turnoRepository.save(existing);
    }

    public void delete(Long id) {
        Turno existing = getById(id);
        turnoRepository.delete(existing);
    }
}
