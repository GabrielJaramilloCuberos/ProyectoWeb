package com.example.vigilapp.services;

import com.example.vigilapp.entities.Rol;
import com.example.vigilapp.exception.RolNotFoundException;
import com.example.vigilapp.repositories.RolRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RolService {

    private final RolRepository rolRepository;

    public RolService(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }

    public List<Rol> getAll() {
        List<Rol> roles = rolRepository.findAll();
        if (roles.isEmpty()) {
            throw new RolNotFoundException("No se encontraron roles");
        }
        return roles;
    }

    public Rol getById(Long id) {
        return rolRepository.findById(id)
                .orElseThrow(() -> new RolNotFoundException("Rol no encontrado con id: " + id));
    }

    public Rol create(Rol rol) {
        return rolRepository.save(rol);
    }

    public Rol update(Long id, Rol rol) {
        Rol existing = getById(id);
        existing.setNombre(rol.getNombre());
        return rolRepository.save(existing);
    }

    public void delete(Long id) {
        Rol existing = getById(id);
        rolRepository.delete(existing);
    }
}
