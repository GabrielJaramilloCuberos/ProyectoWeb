package com.example.vigilapp.controllers;

import com.example.vigilapp.entities.TipoIncidente;
import com.example.vigilapp.services.TipoIncidenteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tipos-incidente")
public class TipoIncidenteController {

    private final TipoIncidenteService tipoIncidenteService;

    public TipoIncidenteController(TipoIncidenteService tipoIncidenteService) {
        this.tipoIncidenteService = tipoIncidenteService;
    }

    @GetMapping
    public ResponseEntity<List<TipoIncidente>> getAll() {
        List<TipoIncidente> tiposIncidente = tipoIncidenteService.getAll();
        return ResponseEntity.status(HttpStatus.OK).body(tiposIncidente);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoIncidente> getById(@PathVariable Long id) {
        TipoIncidente tipoIncidente = tipoIncidenteService.getById(id);
        return ResponseEntity.status(HttpStatus.OK).body(tipoIncidente);
    }

    @PostMapping
    public ResponseEntity<TipoIncidente> create(@Valid @RequestBody TipoIncidente tipoIncidente) {
        TipoIncidente created = tipoIncidenteService.create(tipoIncidente);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TipoIncidente> update(@PathVariable Long id, @Valid @RequestBody TipoIncidente tipoIncidente) {
        TipoIncidente updated = tipoIncidenteService.update(id, tipoIncidente);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tipoIncidenteService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
