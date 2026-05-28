package com.example.vigilapp.repositories;

import com.example.vigilapp.entities.CheckinTurno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CheckinTurnoRepository extends JpaRepository<CheckinTurno, Long> {

    @Query("SELECT c FROM CheckinTurno c " +
           "JOIN FETCH c.turno t " +
           "JOIN FETCH t.docente d " +
           "JOIN FETCH d.rol " +
           "JOIN FETCH t.zona")
    List<CheckinTurno> findAllWithRelations();
}
