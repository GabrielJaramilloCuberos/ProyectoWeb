package com.example.vigilapp.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Data
@Table(name = "Checkpoint")
public class Checkpoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_checkpoint;

    @NotBlank(message = "El nombre del checkpoint es obligatorio")
    private String nombre;

    @NotBlank(message = "El código QR es obligatorio")
    private String codigo_qr;

    @ManyToOne
    @JoinColumn(name = "id_zona", nullable = false)
    @NotNull(message = "La zona es obligatoria")
    private Zona zona;
}