package com.example.vigilapp.services;

import com.example.vigilapp.entities.Recorrido;
import com.example.vigilapp.exception.RecorridoNotFoundException;
import com.example.vigilapp.repositories.RecorridoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecorridoService {

    private final RecorridoRepository recorridoRepository;

    public RecorridoService(RecorridoRepository recorridoRepository) {
        this.recorridoRepository = recorridoRepository;
    }

    public List<Recorrido> getAll() {
        List<Recorrido> recorridos = recorridoRepository.findAll();
        if (recorridos.isEmpty()) {
            throw new RecorridoNotFoundException("No se encontraron recorridos");
        }
        return recorridos;
    }

    public Recorrido getById(Long id) {
        return recorridoRepository.findById(id)
                .orElseThrow(() -> new RecorridoNotFoundException("Recorrido no encontrado con id: " + id));
    }

    public Recorrido create(Recorrido recorrido) {
        return recorridoRepository.save(recorrido);
    }

    public Recorrido update(Long id, Recorrido recorrido) {
        Recorrido existing = getById(id);
        existing.setFecha_hora(recorrido.getFecha_hora());
        existing.setTurno(recorrido.getTurno());
        existing.setCheckpoint(recorrido.getCheckpoint());
        return recorridoRepository.save(existing);
    }

    public void delete(Long id) {
        Recorrido existing = getById(id);
        recorridoRepository.delete(existing);
    }
}
