package com.example.vigilapp.repositories;

import com.example.vigilapp.entities.Recorrido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecorridoRepository extends JpaRepository<Recorrido, Long> {

    @Query("SELECT r FROM Recorrido r " +
           "JOIN FETCH r.turno t " +
           "JOIN FETCH t.docente d " +
           "JOIN FETCH d.rol " +
           "JOIN FETCH t.zona " +
           "JOIN FETCH r.checkpoint cp " +
           "JOIN FETCH cp.zona")
    List<Recorrido> findAllWithRelations();
}
