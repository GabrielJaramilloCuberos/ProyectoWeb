package com.example.vigilapp.services;

import com.example.vigilapp.entities.Checkpoint;
import com.example.vigilapp.exception.CheckpointNotFoundException;
import com.example.vigilapp.repositories.CheckpointRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CheckpointService {

    private final CheckpointRepository checkpointRepository;

    public CheckpointService(CheckpointRepository checkpointRepository) {
        this.checkpointRepository = checkpointRepository;
    }

    @Transactional(readOnly = true)
    public List<Checkpoint> getAll() {
        return checkpointRepository.findAllWithRelations();
    }

    public Checkpoint getById(Long id) {
        return checkpointRepository.findById(id)
                .orElseThrow(() -> new CheckpointNotFoundException("Checkpoint no encontrado con id: " + id));
    }

    public Checkpoint create(Checkpoint checkpoint) {
        return checkpointRepository.save(checkpoint);
    }

    public Checkpoint update(Long id, Checkpoint checkpoint) {
        Checkpoint existing = getById(id);
        existing.setNombre(checkpoint.getNombre());
        existing.setCodigo_qr(checkpoint.getCodigo_qr());
        existing.setZona(checkpoint.getZona());
        return checkpointRepository.save(existing);
    }

    public void delete(Long id) {
        Checkpoint existing = getById(id);
        checkpointRepository.delete(existing);
    }
}
