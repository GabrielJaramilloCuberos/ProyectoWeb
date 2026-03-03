package com.example.vigilapp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.vigilapp.entities.Prueba;
import com.example.vigilapp.repositories.PruebaRepository;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private PruebaRepository repository;

    @GetMapping("/db")
    public String testConnection() {
        Prueba p = new Prueba();
        p.setMensaje("Prueba final con los .env");
        p.setNombre("Dayro Moreno");
        repository.save(p);
        return "Registro guardado en Supabase a las " + p.getCreated_at();
    }
}