package com.example.vigilapp.entities;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Data
@Table(name = "Recorrido")
public class Recorrido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_recorrido;

    @NotNull(message = "La fecha y hora es obligatoria")
    private LocalDateTime fecha_hora;

    @ManyToOne
    @JoinColumn(name = "id_turno", nullable = false)
    @NotNull(message = "El turno es obligatorio")
    private Turno turno;

    @ManyToOne
    @JoinColumn(name = "id_checkpoint", nullable = false)
    @NotNull(message = "El checkpoint es obligatorio")
    private Checkpoint checkpoint;
}