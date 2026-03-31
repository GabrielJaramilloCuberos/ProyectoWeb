package com.example.vigilapp.services;

import com.example.vigilapp.entities.TipoIncidente;
import com.example.vigilapp.exception.TipoIncidenteNotFoundException;
import com.example.vigilapp.repositories.TipoIncidenteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TipoIncidenteService {

    private final TipoIncidenteRepository tipoIncidenteRepository;

    public TipoIncidenteService(TipoIncidenteRepository tipoIncidenteRepository) {
        this.tipoIncidenteRepository = tipoIncidenteRepository;
    }

    public List<TipoIncidente> getAll() {
        List<TipoIncidente> tiposIncidente = tipoIncidenteRepository.findAll();
        if (tiposIncidente.isEmpty()) {
            throw new TipoIncidenteNotFoundException("No se encontraron tipos de incidente");
        }
        return tiposIncidente;
    }

    public TipoIncidente getById(Long id) {
        return tipoIncidenteRepository.findById(id)
                .orElseThrow(() -> new TipoIncidenteNotFoundException("Tipo de incidente no encontrado con id: " + id));
    }

    public TipoIncidente create(TipoIncidente tipoIncidente) {
        return tipoIncidenteRepository.save(tipoIncidente);
    }

    public TipoIncidente update(Long id, TipoIncidente tipoIncidente) {
        TipoIncidente existing = getById(id);
        existing.setNombre(tipoIncidente.getNombre());
        return tipoIncidenteRepository.save(existing);
    }

    public void delete(Long id) {
        TipoIncidente existing = getById(id);
        tipoIncidenteRepository.delete(existing);
    }
}
