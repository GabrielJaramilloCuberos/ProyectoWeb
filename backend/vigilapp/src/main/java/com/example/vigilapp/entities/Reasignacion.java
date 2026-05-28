package com.example.vigilapp.entities;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Data
@Table(name = "reasignacion")
public class Reasignacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_reasignacion;

    @NotBlank(message = "El motivo es obligatorio")
    private String motivo;

    @NotNull(message = "La fecha de propuesta es obligatoria")
    private LocalDateTime fecha_propuesta;

    private LocalDateTime fecha_respuesta;

    @NotBlank(message = "El estado es obligatorio")
    private String estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_turno", nullable = false)
    @NotNull(message = "El turno es obligatorio")
    private Turno turno;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_docente_original", nullable = false)
    @NotNull(message = "El docente original es obligatorio")
    private Usuario docenteOriginal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_docente_propuesto", nullable = false)
    @NotNull(message = "El docente propuesto es obligatorio")
    private Usuario docentePropuesto;
}