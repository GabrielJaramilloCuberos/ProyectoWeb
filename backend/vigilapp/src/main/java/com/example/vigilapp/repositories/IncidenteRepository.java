package com.example.vigilapp.repositories;

import com.example.vigilapp.entities.Incidente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IncidenteRepository extends JpaRepository<Incidente, Long> {

    @Query("SELECT i FROM Incidente i " +
           "JOIN FETCH i.turno t " +
           "JOIN FETCH t.docente d " +
           "JOIN FETCH d.rol " +
           "JOIN FETCH t.zona " +
           "JOIN FETCH i.zona " +
           "JOIN FETCH i.tipoIncidente " +
           "JOIN FETCH i.severidad")
    List<Incidente> findAllWithRelations();

    @Query("SELECT i FROM Incidente i " +
           "JOIN FETCH i.turno t " +
           "JOIN FETCH t.docente d " +
           "JOIN FETCH d.rol " +
           "JOIN FETCH t.zona " +
           "JOIN FETCH i.zona " +
           "JOIN FETCH i.tipoIncidente " +
           "JOIN FETCH i.severidad " +
           "WHERE i.id_incidente = :id")
    Optional<Incidente> findByIdWithRelations(Long id);
}
