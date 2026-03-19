package com.example.vigilapp.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Data
@Table(name = "MetricasDocente")
public class MetricasDocente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_metrica;

    @NotBlank(message = "El periodo es obligatorio")
    private String periodo;

    @NotNull(message = "El porcentaje de puntualidad es obligatorio")
    private Double puntualidad_porcentaje;

    @NotNull(message = "El promedio de recorridos es obligatorio")
    private Double recorridos_promedio;

    @NotNull(message = "Los incidentes reportados son obligatorios")
    private Integer incidentes_reportados;

    @NotNull(message = "Los puntos totales son obligatorios")
    private Integer puntos_totales;

    @ManyToOne
    @JoinColumn(name = "id_docente", nullable = false)
    @NotNull(message = "El docente es obligatorio")
    private Usuario docente;
}