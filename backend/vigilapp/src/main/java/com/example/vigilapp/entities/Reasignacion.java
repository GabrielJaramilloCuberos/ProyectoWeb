package com.example.vigilapp.entities;

import java.time.LocalDateTime;

import org.hibernate.annotations.ManyToAny;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "Reasignacion")
public class Reasignacion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_reasignacion;

    private String motivo;
    private LocalDateTime fecha_propuesta;
    private LocalDateTime fecha_respuesta;
    private String estado;

    @ManyToAny(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_turno",nullable = false)
    private Long id_turno;

    @ManyToAny(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_docente_original",nullable = false)
    private Long id_docente_original;

    @ManyToAny(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_docente_propuesto",nullable = false)
    private Long id_docente_propuesto;
    

}

