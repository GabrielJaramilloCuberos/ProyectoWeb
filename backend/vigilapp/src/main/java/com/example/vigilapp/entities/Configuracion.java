package com.example.vigilapp.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "configuracion_sistema")
public class Configuracion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer shiftDuration;
    private Integer minPatrols;
    private Integer notificationMinutes;
    private Integer autoReassignMinutes;
    private Boolean gamificationEnabled;
    private Integer pointsPerShift;
    private Integer pointsPerPatrol;
    private Integer pointsPerReport;
}
