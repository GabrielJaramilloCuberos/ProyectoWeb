package com.example.vigilapp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.vigilapp.entities.Prueba;

public interface PruebaRepository extends JpaRepository<Prueba, Long> {
}