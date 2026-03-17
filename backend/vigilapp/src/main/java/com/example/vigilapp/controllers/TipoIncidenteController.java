package com.example.vigilapp.controllers;

import com.example.vigilapp.entities.TipoIncidente;
import com.example.vigilapp.exception.TipoIncidenteNotFoundException;
import com.example.vigilapp.repositories.TipoIncidenteRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<List<TipoIncidente>> getAll() {
        List<TipoIncidente> tiposIncidente = tipoIncidenteRepository.findAll();
        if (tiposIncidente.isEmpty()) {
            throw new TipoIncidenteNotFoundException("No se encontraron tipos de incidente");
        }
        return ResponseEntity.status(HttpStatus.OK).body(tiposIncidente);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoIncidente> getById(@PathVariable Long id) {
        TipoIncidente tipoIncidente = tipoIncidenteRepository.findById(id)
                .orElseThrow(() -> new TipoIncidenteNotFoundException("Tipo de incidente no encontrado con id: " + id));
        return ResponseEntity.status(HttpStatus.OK).body(tipoIncidente);
    }

    @PostMapping
    public ResponseEntity<TipoIncidente> create(@Valid @RequestBody TipoIncidente tipoIncidente) {
        TipoIncidente created = tipoIncidenteRepository.save(tipoIncidente);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TipoIncidente> update(@PathVariable Long id, @Valid @RequestBody TipoIncidente tipoIncidente) {
        TipoIncidente existing = tipoIncidenteRepository.findById(id)
                .orElseThrow(() -> new TipoIncidenteNotFoundException("Tipo de incidente no encontrado con id: " + id));
        existing.setNombre(tipoIncidente.getNombre());
        TipoIncidente updated = tipoIncidenteRepository.save(existing);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        TipoIncidente existing = tipoIncidenteRepository.findById(id)
                .orElseThrow(() -> new TipoIncidenteNotFoundException("Tipo de incidente no encontrado con id: " + id));
        tipoIncidenteRepository.delete(existing);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
