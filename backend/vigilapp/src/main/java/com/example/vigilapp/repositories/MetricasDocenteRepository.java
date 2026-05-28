package com.example.vigilapp.repositories;

import com.example.vigilapp.entities.MetricasDocente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MetricasDocenteRepository extends JpaRepository<MetricasDocente, Long> {

    @Query("SELECT m FROM MetricasDocente m " +
           "JOIN FETCH m.docente d " +
           "JOIN FETCH d.rol")
    List<MetricasDocente> findAllWithRelations();
}
