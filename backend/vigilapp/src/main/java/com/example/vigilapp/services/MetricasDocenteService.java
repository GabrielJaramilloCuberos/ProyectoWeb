package com.example.vigilapp.services;

import com.example.vigilapp.entities.MetricasDocente;
import com.example.vigilapp.exception.MetricasDocenteNotFoundException;
import com.example.vigilapp.repositories.MetricasDocenteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MetricasDocenteService {

    private final MetricasDocenteRepository metricasDocenteRepository;

    public MetricasDocenteService(MetricasDocenteRepository metricasDocenteRepository) {
        this.metricasDocenteRepository = metricasDocenteRepository;
    }

    public List<MetricasDocente> getAll() {
        List<MetricasDocente> metricas = metricasDocenteRepository.findAll();
        if (metricas.isEmpty()) {
            throw new MetricasDocenteNotFoundException("No se encontraron metricas docente");
        }
        return metricas;
    }

    public MetricasDocente getById(Long id) {
        return metricasDocenteRepository.findById(id)
                .orElseThrow(() -> new MetricasDocenteNotFoundException("Metrica docente no encontrada con id: " + id));
    }

    public MetricasDocente create(MetricasDocente metricasDocente) {
        return metricasDocenteRepository.save(metricasDocente);
    }

    public MetricasDocente update(Long id, MetricasDocente metricasDocente) {
        MetricasDocente existing = getById(id);
        existing.setPeriodo(metricasDocente.getPeriodo());
        existing.setPuntualidad_porcentaje(metricasDocente.getPuntualidad_porcentaje());
        existing.setRecorridos_promedio(metricasDocente.getRecorridos_promedio());
        existing.setIncidentes_reportados(metricasDocente.getIncidentes_reportados());
        existing.setPuntos_totales(metricasDocente.getPuntos_totales());
        existing.setDocente(metricasDocente.getDocente());
        return metricasDocenteRepository.save(existing);
    }

    public void delete(Long id) {
        MetricasDocente existing = getById(id);
        metricasDocenteRepository.delete(existing);
    }
}
