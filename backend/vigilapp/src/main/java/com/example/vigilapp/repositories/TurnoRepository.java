package com.example.vigilapp.repositories;

import com.example.vigilapp.entities.Turno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TurnoRepository extends JpaRepository<Turno, Long> {

    @Query("SELECT t FROM Turno t " +
           "JOIN FETCH t.docente d " +
           "JOIN FETCH d.rol " +
           "JOIN FETCH t.zona")
    List<Turno> findAllWithRelations();

    @Query("SELECT t FROM Turno t " +
           "JOIN FETCH t.docente d " +
           "JOIN FETCH d.rol " +
           "JOIN FETCH t.zona " +
           "WHERE t.id_turno = :id")
    Optional<Turno> findByIdWithRelations(Long id);
}
