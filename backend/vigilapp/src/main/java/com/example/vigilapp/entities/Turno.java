package com.example.vigilapp.entities;

import java.time.LocalDate;
import java.time.LocalTime;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Data
@Table(name = "Turno")
public class Turno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_turno;

    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;

    @NotNull(message = "La hora de inicio es obligatoria")
    private LocalTime hora_inicio;

    @NotNull(message = "La hora de fin es obligatoria")
    private LocalTime hora_fin;

    @NotBlank(message = "El estado es obligatorio")
    private String estado;

    @NotNull
    private Integer limpieza_calificacion;

    @ManyToOne
    @JoinColumn(name = "id_docente", nullable = false)
    @NotNull(message = "El docente es obligatorio")
    private Usuario docente;

    @ManyToOne
    @JoinColumn(name = "id_zona", nullable = false)
    @NotNull(message = "La zona es obligatoria")
    private Zona zona;
}