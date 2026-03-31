package com.example.vigilapp.services;

import com.example.vigilapp.entities.CheckinTurno;
import com.example.vigilapp.exception.CheckinTurnoNotFoundException;
import com.example.vigilapp.repositories.CheckinTurnoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CheckinTurnoService {

    private final CheckinTurnoRepository checkinTurnoRepository;

    public CheckinTurnoService(CheckinTurnoRepository checkinTurnoRepository) {
        this.checkinTurnoRepository = checkinTurnoRepository;
    }

    public List<CheckinTurno> getAll() {
        List<CheckinTurno> checkins = checkinTurnoRepository.findAll();
        if (checkins.isEmpty()) {
            throw new CheckinTurnoNotFoundException("No se encontraron checkins de turno");
        }
        return checkins;
    }

    public CheckinTurno getById(Long id) {
        return checkinTurnoRepository.findById(id)
                .orElseThrow(() -> new CheckinTurnoNotFoundException("Checkin de turno no encontrado con id: " + id));
    }

    public CheckinTurno create(CheckinTurno checkinTurno) {
        return checkinTurnoRepository.save(checkinTurno);
    }

    public CheckinTurno update(Long id, CheckinTurno checkinTurno) {
        CheckinTurno existing = getById(id);
        existing.setFecha_hora(checkinTurno.getFecha_hora());
        existing.setMetodo(checkinTurno.getMetodo());
        existing.setTurno(checkinTurno.getTurno());
        return checkinTurnoRepository.save(existing);
    }

    public void delete(Long id) {
        CheckinTurno existing = getById(id);
        checkinTurnoRepository.delete(existing);
    }
}
