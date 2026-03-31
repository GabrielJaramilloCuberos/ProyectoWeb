package com.example.vigilapp.services;

import com.example.vigilapp.entities.Incidente;
import com.example.vigilapp.exception.IncidenteNotFoundException;
import com.example.vigilapp.repositories.IncidenteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IncidenteService {

    private final IncidenteRepository incidenteRepository;

    public IncidenteService(IncidenteRepository incidenteRepository) {
        this.incidenteRepository = incidenteRepository;
    }

    public List<Incidente> getAll() {
        List<Incidente> incidentes = incidenteRepository.findAll();
        if (incidentes.isEmpty()) {
            throw new IncidenteNotFoundException("No se encontraron incidentes");
        }
        return incidentes;
    }

    public Incidente getById(Long id) {
        return incidenteRepository.findById(id)
                .orElseThrow(() -> new IncidenteNotFoundException("Incidente no encontrado con id: " + id));
    }

    public Incidente create(Incidente incidente) {
        return incidenteRepository.save(incidente);
    }

    public Incidente update(Long id, Incidente incidente) {
        Incidente existing = getById(id);
        existing.setFecha_hora(incidente.getFecha_hora());
        existing.setDescripcion(incidente.getDescripcion());
        existing.setTurno(incidente.getTurno());
        existing.setZona(incidente.getZona());
        existing.setTipoIncidente(incidente.getTipoIncidente());
        existing.setSeveridad(incidente.getSeveridad());
        return incidenteRepository.save(existing);
    }

    public void delete(Long id) {
        Incidente existing = getById(id);
        incidenteRepository.delete(existing);
    }
}
