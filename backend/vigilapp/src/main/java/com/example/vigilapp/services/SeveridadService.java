package com.example.vigilapp.services;

import com.example.vigilapp.entities.Severidad;
import com.example.vigilapp.exception.SeveridadNotFoundException;
import com.example.vigilapp.repositories.SeveridadRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeveridadService {

    private final SeveridadRepository severidadRepository;

    public SeveridadService(SeveridadRepository severidadRepository) {
        this.severidadRepository = severidadRepository;
    }

    public List<Severidad> getAll() {
        List<Severidad> severidades = severidadRepository.findAll();
        if (severidades.isEmpty()) {
            throw new SeveridadNotFoundException("No se encontraron severidades");
        }
        return severidades;
    }

    public Severidad getById(Long id) {
        return severidadRepository.findById(id)
                .orElseThrow(() -> new SeveridadNotFoundException("Severidad no encontrada con id: " + id));
    }

    public Severidad create(Severidad severidad) {
        return severidadRepository.save(severidad);
    }

    public Severidad update(Long id, Severidad severidad) {
        Severidad existing = getById(id);
        existing.setCodigo(severidad.getCodigo());
        existing.setDescripcion(severidad.getDescripcion());
        return severidadRepository.save(existing);
    }

    public void delete(Long id) {
        Severidad existing = getById(id);
        severidadRepository.delete(existing);
    }
}
