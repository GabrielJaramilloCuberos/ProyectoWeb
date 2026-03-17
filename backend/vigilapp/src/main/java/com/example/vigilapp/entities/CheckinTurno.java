package com.example.vigilapp.entities;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Data
@Table(name = "CheckinTurno")
public class CheckinTurno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_checkin;

    @NotNull(message = "La fecha y hora es obligatoria")
    private LocalDateTime fecha_hora;

    @NotBlank(message = "El método es obligatorio")
    private String metodo;

    @ManyToOne
    @JoinColumn(name = "id_turno", nullable = false)
    @NotNull(message = "El turno es obligatorio")
    private Turno turno;
}