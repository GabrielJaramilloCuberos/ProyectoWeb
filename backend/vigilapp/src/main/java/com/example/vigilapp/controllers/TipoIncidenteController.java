package com.example.vigilapp.controllers;

import com.example.vigilapp.entities.TipoIncidente;
import com.example.vigilapp.repositories.TipoIncidenteRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tipos-incidente")
public class TipoIncidenteController {

    private final TipoIncidenteRepository tipoIncidenteRepository;

    public TipoIncidenteController(TipoIncidenteRepository tipoIncidenteRepository) {
        this.tipoIncidenteRepository = tipoIncidenteRepository;
    }

    @GetMapping
    public List<TipoIncidente> getAll() {
        return tipoIncidenteRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoIncidente> getById(@PathVariable Long id) {
        return tipoIncidenteRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public TipoIncidente create(@RequestBody TipoIncidente tipoIncidente) {
        return tipoIncidenteRepository.save(tipoIncidente);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TipoIncidente> update(@PathVariable Long id, @RequestBody TipoIncidente tipoIncidente) {
        return tipoIncidenteRepository.findById(id).map(existing -> {
            existing.setNombre(tipoIncidente.getNombre());
            return ResponseEntity.ok(tipoIncidenteRepository.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return tipoIncidenteRepository.findById(id).map(existing -> {
            tipoIncidenteRepository.delete(existing);
            return ResponseEntity.ok().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }
}
