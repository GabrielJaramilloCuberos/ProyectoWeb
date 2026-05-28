package com.example.vigilapp.repositories;

import com.example.vigilapp.entities.Reasignacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReasignacionRepository extends JpaRepository<Reasignacion, Long> {

    @Query("SELECT r FROM Reasignacion r " +
           "JOIN FETCH r.turno t " +
           "JOIN FETCH t.docente td " +
           "JOIN FETCH td.rol " +
           "JOIN FETCH t.zona " +
           "JOIN FETCH r.docenteOriginal dOrig " +
           "JOIN FETCH dOrig.rol " +
           "JOIN FETCH r.docentePropuesto dProp " +
           "JOIN FETCH dProp.rol")
    List<Reasignacion> findAllWithRelations();

    @Query("SELECT r FROM Reasignacion r " +
           "JOIN FETCH r.turno t " +
           "JOIN FETCH t.docente td " +
           "JOIN FETCH td.rol " +
           "JOIN FETCH t.zona " +
           "JOIN FETCH r.docenteOriginal dOrig " +
           "JOIN FETCH dOrig.rol " +
           "JOIN FETCH r.docentePropuesto dProp " +
           "JOIN FETCH dProp.rol " +
           "WHERE r.id_reasignacion = :id")
    Optional<Reasignacion> findByIdWithRelations(Long id);
}
