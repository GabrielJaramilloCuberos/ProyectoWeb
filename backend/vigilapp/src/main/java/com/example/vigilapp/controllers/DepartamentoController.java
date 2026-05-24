package com.example.vigilapp.controllers;

import com.example.vigilapp.entities.Departamento;
import com.example.vigilapp.services.DepartamentoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departamentos")
public class DepartamentoController {

    private final DepartamentoService departamentoService;

    public DepartamentoController(DepartamentoService departamentoService) {
        this.departamentoService = departamentoService;
    }

    @GetMapping
    public ResponseEntity<List<Departamento>> getAll() {
        List<Departamento> departamentos = departamentoService.getAll();
        return ResponseEntity.status(HttpStatus.OK).body(departamentos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Departamento> getById(@PathVariable Long id) {
        Departamento departamento = departamentoService.getById(id);
        return ResponseEntity.status(HttpStatus.OK).body(departamento);
    }

    @PostMapping
    public ResponseEntity<Departamento> create(@Valid @RequestBody Departamento departamento) {
        Departamento created = departamentoService.create(departamento);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Departamento> update(@PathVariable Long id, @Valid @RequestBody Departamento departamento) {
        Departamento updated = departamentoService.update(id, departamento);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        departamentoService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
