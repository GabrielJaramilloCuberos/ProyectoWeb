package com.example.vigilapp.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Data
@Table(name = "Zona")
public class Zona {
      @Id
      @GeneratedValue(strategy = GenerationType.IDENTITY)
      private Long id_zona;

      @NotBlank(message = "El nombre de la zona es obligatorio")
      private String nombre;

      @NotBlank(message = "La descripción de la zona es obligatoria")
      private String descripcion;

      @NotBlank(message = "El tipo de zona es obligatorio")
      private String tipo;

      @NotNull(message = "El estado de la zona es obligatorio")
      private Boolean activa;
}
