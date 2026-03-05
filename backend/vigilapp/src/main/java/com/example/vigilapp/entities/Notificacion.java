package com.example.vigilapp.entities;

import java.time.LocalDateTime;

import org.hibernate.annotations.ManyToAny;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "Notificacion")
public class Notificacion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_notificacion;

    private String mensaje;
    private String tipo;
    private LocalDateTime fecha_envio;
    private Boolean leida;
    
    //Cambiar dependiendo de la relacion, esto aplica para las otras entidades :)
    @ManyToAny(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Long id_usuario;
    

}
