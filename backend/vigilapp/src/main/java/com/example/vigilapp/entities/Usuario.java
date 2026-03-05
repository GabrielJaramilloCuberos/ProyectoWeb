package com.example.vigilapp.entities;

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
@Table(name = "Usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_usuario;
    
    private String nombre;
    private String email;
    private String password;
    private Boolean estado;


    @ManyToAny(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_rol", nullable = false)
    private Long id_rol;
    
}
