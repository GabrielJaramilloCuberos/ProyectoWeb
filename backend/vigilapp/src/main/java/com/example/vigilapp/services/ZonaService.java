package com.example.vigilapp.services;

import com.example.vigilapp.entities.Zona;
import com.example.vigilapp.exception.ZonaNotFoundException;
import com.example.vigilapp.repositories.ZonaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ZonaService {

    private final ZonaRepository zonaRepository;

    public ZonaService(ZonaRepository zonaRepository) {
        this.zonaRepository = zonaRepository;
    }

    public List<Zona> getAll() {
        List<Zona> zonas = zonaRepository.findAll();
        if (zonas.isEmpty()) {
            throw new ZonaNotFoundException("No se encontraron zonas");
        }
        return zonas;
    }

    public Zona getById(Long id) {
        return zonaRepository.findById(id)
                .orElseThrow(() -> new ZonaNotFoundException("Zona no encontrada con id: " + id));
    }

    public Zona create(Zona zona) {
        return zonaRepository.save(zona);
    }

    public Zona update(Long id, Zona zona) {
        Zona existing = getById(id);
        existing.setNombre(zona.getNombre());
        existing.setDescripcion(zona.getDescripcion());
        existing.setTipo(zona.getTipo());
        existing.setActiva(zona.getActiva());
        return zonaRepository.save(existing);
    }

    public void delete(Long id) {
        Zona existing = getById(id);
        zonaRepository.delete(existing);
    }
}
