package com.example.vigilapp.services;

import com.example.vigilapp.entities.Departamento;
import com.example.vigilapp.exception.DepartamentoNotFoundException;
import com.example.vigilapp.repositories.DepartamentoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartamentoService {

    private final DepartamentoRepository departamentoRepository;

    public DepartamentoService(DepartamentoRepository departamentoRepository) {
        this.departamentoRepository = departamentoRepository;
    }

    public List<Departamento> getAll() {
        return departamentoRepository.findAll();
    }

    public Departamento getById(Long id) {
        return departamentoRepository.findById(id)
                .orElseThrow(() -> new DepartamentoNotFoundException("Departamento no encontrado con id: " + id));
    }

    public Departamento create(Departamento departamento) {
        return departamentoRepository.save(departamento);
    }

    public Departamento update(Long id, Departamento departamento) {
        Departamento existing = getById(id);
        existing.setNombre(departamento.getNombre());
        return departamentoRepository.save(existing);
    }

    public void delete(Long id) {
        Departamento existing = getById(id);
        departamentoRepository.delete(existing);
    }
}
