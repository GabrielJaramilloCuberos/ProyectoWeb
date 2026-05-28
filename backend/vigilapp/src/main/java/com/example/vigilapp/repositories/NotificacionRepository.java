package com.example.vigilapp.repositories;

import com.example.vigilapp.entities.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    @Query("SELECT n FROM Notificacion n " +
           "JOIN FETCH n.usuario u " +
           "JOIN FETCH u.rol")
    List<Notificacion> findAllWithRelations();
}
