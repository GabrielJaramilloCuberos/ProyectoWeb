package com.example.vigilapp.services;

import com.example.vigilapp.entities.Reasignacion;
import com.example.vigilapp.entities.Turno;
import com.example.vigilapp.exception.ReasignacionNotFoundException;
import com.example.vigilapp.exception.TurnoNotFoundException;
import com.example.vigilapp.repositories.ReasignacionRepository;
import com.example.vigilapp.repositories.TurnoRepository;
import com.example.vigilapp.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReasignacionService {

    private final ReasignacionRepository reasignacionRepository;
    private final TurnoRepository turnoRepository;
    private final UsuarioRepository usuarioRepository;

    public ReasignacionService(ReasignacionRepository reasignacionRepository,
                               TurnoRepository turnoRepository,
                               UsuarioRepository usuarioRepository) {
        this.reasignacionRepository = reasignacionRepository;
        this.turnoRepository        = turnoRepository;
        this.usuarioRepository      = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<Reasignacion> getAll() {
        return reasignacionRepository.findAllWithRelations();
    }

    @Transactional(readOnly = true)
    public Reasignacion getById(Long id) {
        return reasignacionRepository.findByIdWithRelations(id)
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

    @Transactional
    public Reasignacion update(Long id, Reasignacion reasignacion) {
        Reasignacion existing = getById(id);
        existing.setMotivo(reasignacion.getMotivo());
        existing.setFecha_propuesta(reasignacion.getFecha_propuesta());
        existing.setFecha_respuesta(reasignacion.getFecha_respuesta());
        existing.setEstado(reasignacion.getEstado());
        if (reasignacion.getTurno() != null && reasignacion.getTurno().getId_turno() != null)
            existing.setTurno(turnoRepository.getReferenceById(reasignacion.getTurno().getId_turno()));
        if (reasignacion.getDocenteOriginal() != null && reasignacion.getDocenteOriginal().getId_usuario() != null)
            existing.setDocenteOriginal(usuarioRepository.getReferenceById(reasignacion.getDocenteOriginal().getId_usuario()));
        if (reasignacion.getDocentePropuesto() != null && reasignacion.getDocentePropuesto().getId_usuario() != null)
            existing.setDocentePropuesto(usuarioRepository.getReferenceById(reasignacion.getDocentePropuesto().getId_usuario()));
        reasignacionRepository.save(existing);
        return getById(id);
    }

    public void delete(Long id) {
        Reasignacion existing = getById(id);
        reasignacionRepository.delete(existing);
    }
}
