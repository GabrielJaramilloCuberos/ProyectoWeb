package com.example.vigilapp.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Entity
@Data
@Table(name = "TipoIncidente")
public class TipoIncidente {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_tipo;

    @NotBlank(message = "El nombre del tipo de incidente es obligatorio")
    private String nombre;
}
