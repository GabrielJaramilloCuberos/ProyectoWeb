package com.example.vigilapp.services;

import com.example.vigilapp.entities.Incidente;
import com.example.vigilapp.exception.IncidenteNotFoundException;
import com.example.vigilapp.repositories.IncidenteRepository;
import com.example.vigilapp.repositories.TurnoRepository;
import com.example.vigilapp.repositories.ZonaRepository;
import com.example.vigilapp.repositories.TipoIncidenteRepository;
import com.example.vigilapp.repositories.SeveridadRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class IncidenteService {

    private final IncidenteRepository incidenteRepository;
    private final TurnoRepository turnoRepository;
    private final ZonaRepository zonaRepository;
    private final TipoIncidenteRepository tipoIncidenteRepository;
    private final SeveridadRepository severidadRepository;

    public IncidenteService(IncidenteRepository incidenteRepository,
                            TurnoRepository turnoRepository,
                            ZonaRepository zonaRepository,
                            TipoIncidenteRepository tipoIncidenteRepository,
                            SeveridadRepository severidadRepository) {
        this.incidenteRepository      = incidenteRepository;
        this.turnoRepository          = turnoRepository;
        this.zonaRepository           = zonaRepository;
        this.tipoIncidenteRepository  = tipoIncidenteRepository;
        this.severidadRepository      = severidadRepository;
    }

    @Transactional(readOnly = true)
    public List<Incidente> getAll() {
        return incidenteRepository.findAllWithRelations();
    }

    @Transactional(readOnly = true)
    public Incidente getById(Long id) {
        return incidenteRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new IncidenteNotFoundException("Incidente no encontrado con id: " + id));
    }

    public Incidente create(Incidente incidente) {
        return incidenteRepository.save(incidente);
    }

    @Transactional
    public Incidente update(Long id, Incidente incidente) {
        Incidente existing = getById(id);
        existing.setFecha_hora(incidente.getFecha_hora());
        existing.setDescripcion(incidente.getDescripcion());
        if (incidente.getTurno() != null && incidente.getTurno().getId_turno() != null)
            existing.setTurno(turnoRepository.getReferenceById(incidente.getTurno().getId_turno()));
        if (incidente.getZona() != null && incidente.getZona().getId_zona() != null)
            existing.setZona(zonaRepository.getReferenceById(incidente.getZona().getId_zona()));
        if (incidente.getTipoIncidente() != null && incidente.getTipoIncidente().getId_tipo() != null)
            existing.setTipoIncidente(tipoIncidenteRepository.getReferenceById(incidente.getTipoIncidente().getId_tipo()));
        if (incidente.getSeveridad() != null && incidente.getSeveridad().getId_severidad() != null)
            existing.setSeveridad(severidadRepository.getReferenceById(incidente.getSeveridad().getId_severidad()));
        incidenteRepository.save(existing);
        return getById(id);
    }

    public void delete(Long id) {
        Incidente existing = getById(id);
        incidenteRepository.delete(existing);
    }
}
