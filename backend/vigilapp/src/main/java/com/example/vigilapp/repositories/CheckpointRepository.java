package com.example.vigilapp.repositories;

import com.example.vigilapp.entities.Checkpoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CheckpointRepository extends JpaRepository<Checkpoint, Long> {

    @Query("SELECT cp FROM Checkpoint cp JOIN FETCH cp.zona")
    List<Checkpoint> findAllWithRelations();
}
