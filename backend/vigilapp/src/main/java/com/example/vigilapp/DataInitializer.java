package com.example.vigilapp;

import com.example.vigilapp.entities.MetricasDocente;
import com.example.vigilapp.entities.Rol;
import com.example.vigilapp.entities.Usuario;
import com.example.vigilapp.repositories.MetricasDocenteRepository;
import com.example.vigilapp.repositories.RolRepository;
import com.example.vigilapp.repositories.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RolRepository rolRepo;
    private final UsuarioRepository usuarioRepo;
    private final MetricasDocenteRepository metricasRepo;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RolRepository rolRepo,
                           UsuarioRepository usuarioRepo,
                           MetricasDocenteRepository metricasRepo,
                           PasswordEncoder passwordEncoder) {
        this.rolRepo          = rolRepo;
        this.usuarioRepo      = usuarioRepo;
        this.metricasRepo     = metricasRepo;
        this.passwordEncoder  = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        Optional<Rol> rolProfesor = rolRepo.findAll().stream()
                .filter(r -> r.getNombre().equalsIgnoreCase("PROFESOR"))
                .findFirst();

        if (rolProfesor.isEmpty()) return;

        Rol rol = rolProfesor.get();

        List<ProfesorSeed> seeds = List.of(
            new ProfesorSeed("Ana María Torres",     "ana.torres@colegio.edu.co",       95.0, 8.5, 5, 420),
            new ProfesorSeed("Luis Hernández",       "luis.hernandez@colegio.edu.co",   88.0, 7.2, 3, 360),
            new ProfesorSeed("Marcela Gómez",        "marcela.gomez@colegio.edu.co",    92.0, 9.1, 7, 490),
            new ProfesorSeed("Camilo Rodríguez",     "camilo.rodriguez@colegio.edu.co", 80.0, 6.8, 2, 300),
            new ProfesorSeed("Valentina Castro",     "valentina.castro@colegio.edu.co", 97.0, 9.8, 9, 560),
            new ProfesorSeed("Sergio Patiño",        "sergio.patino@colegio.edu.co",    75.0, 5.5, 1, 240),
            new ProfesorSeed("Diana Morales",        "diana.morales@colegio.edu.co",    89.0, 8.0, 4, 390)
        );

        for (ProfesorSeed seed : seeds) {
            if (usuarioRepo.findByEmail(seed.email()).isEmpty()) {
                Usuario u = new Usuario();
                u.setNombre(seed.nombre());
                u.setEmail(seed.email());
                u.setPassword(passwordEncoder.encode("docente123"));
                u.setEstado(true);
                u.setRol(rol);
                Usuario saved = usuarioRepo.save(u);

                MetricasDocente m = new MetricasDocente();
                m.setPeriodo("2026-05");
                m.setPuntualidad_porcentaje(seed.puntualidad());
                m.setRecorridos_promedio(seed.recorridos());
                m.setIncidentes_reportados(seed.incidentes());
                m.setPuntos_totales(seed.puntos());
                m.setDocente(saved);
                metricasRepo.save(m);
            }
        }
    }

    private record ProfesorSeed(
        String nombre, String email,
        double puntualidad, double recorridos,
        int incidentes, int puntos
    ) {}
}
