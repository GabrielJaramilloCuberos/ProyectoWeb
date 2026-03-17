package com.example.vigilapp.entities;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Data
@Table(name = "Incidente")
public class Incidente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_incidente;

    @NotNull(message = "La fecha y hora es obligatoria")
    private LocalDateTime fecha_hora;

    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;

    @ManyToOne
    @JoinColumn(name = "id_turno", nullable = false)
    @NotNull(message = "El turno es obligatorio")
    private Turno turno;

    @ManyToOne
    @JoinColumn(name = "id_zona", nullable = false)
    @NotNull(message = "La zona es obligatoria")
    private Zona zona;

    @ManyToOne
    @JoinColumn(name = "id_tipo", nullable = false)
    @NotNull(message = "El tipo de incidente es obligatorio")
    private TipoIncidente tipoIncidente;

    @ManyToOne
    @JoinColumn(name = "id_severidad", nullable = false)
    @NotNull(message = "La severidad es obligatoria")
    private Severidad severidad;
}