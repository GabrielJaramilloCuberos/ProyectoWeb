package com.example.vigilapp.controllers;

import com.example.vigilapp.entities.Turno;
import com.example.vigilapp.entities.Usuario;
import com.example.vigilapp.entities.Zona;
import com.example.vigilapp.services.TurnoService;
import com.example.vigilapp.services.UsuarioService;
import com.example.vigilapp.services.ZonaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    private final ZonaService zonaService;
    private final TurnoService turnoService;
    private final UsuarioService usuarioService;

    public AdminController(ZonaService zonaService,
                           TurnoService turnoService,
                           UsuarioService usuarioService) {
        this.zonaService    = zonaService;
        this.turnoService   = turnoService;
        this.usuarioService = usuarioService;
    }

    // ─────────────────────────────────────────
    // ZONES  →  /api/admin/zones
    // ─────────────────────────────────────────

    @GetMapping("/zones")
    public ResponseEntity<List<Map<String, Object>>> getZones() {
        List<Map<String, Object>> result;
        try {
            result = zonaService.getAll().stream()
                    .map(this::zoneToMap)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            result = List.of();
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/zones")
    public ResponseEntity<Map<String, Object>> createZone(@RequestBody Map<String, Object> body) {
        Zona zona = new Zona();
        zona.setNombre((String) body.get("name"));
        zona.setDescripcion((String) body.getOrDefault("description", ""));
        zona.setTipo("vigilancia");
        zona.setActiva(true);
        Zona saved = zonaService.create(zona);
        return ResponseEntity.status(201).body(zoneToMap(saved));
    }

    @PatchMapping("/zones/{id}")
    public ResponseEntity<Map<String, Object>> updateZone(@PathVariable Long id,
                                                           @RequestBody Map<String, Object> body) {
        Zona zona = zonaService.getById(id);
        if (body.containsKey("name"))        zona.setNombre((String) body.get("name"));
        if (body.containsKey("description")) zona.setDescripcion((String) body.get("description"));
        if (body.containsKey("isActive"))    zona.setActiva((Boolean) body.get("isActive"));
        Zona updated = zonaService.update(id, zona);
        return ResponseEntity.ok(zoneToMap(updated));
    }

    @DeleteMapping("/zones/{id}")
    public ResponseEntity<Void> deleteZone(@PathVariable Long id) {
        zonaService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private Map<String, Object> zoneToMap(Zona z) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id",          String.valueOf(z.getId_zona()));
        m.put("name",        z.getNombre());
        m.put("description", z.getDescripcion() != null ? z.getDescripcion() : "");
        m.put("checkpoints", List.of());
        m.put("isActive",    z.getActiva());
        return m;
    }

    // ─────────────────────────────────────────
    // TEACHERS  →  /api/admin/teachers
    // ─────────────────────────────────────────

    @GetMapping("/teachers")
    public ResponseEntity<List<Map<String, Object>>> getTeachers() {
        List<Map<String, Object>> result;
        try {
            result = usuarioService.getAll().stream()
                    .filter(u -> u.getRol() != null &&
                                 u.getRol().getNombre().equalsIgnoreCase("DOCENTE"))
                    .map(this::teacherToMap)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            // UsuarioService lanza excepción si no hay usuarios — retornamos lista vacía
            result = List.of();
        }
        return ResponseEntity.ok(result);
    }

    private Map<String, Object> teacherToMap(Usuario u) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id",         String.valueOf(u.getId_usuario()));
        m.put("name",       u.getNombre());   // Usuario solo tiene nombre, sin apellido
        m.put("department", "General");        // No existe en la entidad
        m.put("isActive",   u.getEstado() != null ? u.getEstado() : true);
        return m;
    }

    // ─────────────────────────────────────────
    // SHIFTS  →  /api/admin/shifts
    // ─────────────────────────────────────────

    @GetMapping("/shifts")
    public ResponseEntity<List<Map<String, Object>>> getShifts() {
        List<Map<String, Object>> result;
        try {
            result = turnoService.getAll().stream()
                    .map(this::shiftToMap)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            result = List.of();
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/shifts")
    public ResponseEntity<Map<String, Object>> createShift(@RequestBody Map<String, Object> body) {
        Turno turno = new Turno();

        Long zonaId    = Long.valueOf(body.get("zoneId").toString());
        Long docenteId = Long.valueOf(body.get("teacherId").toString());

        turno.setZona(zonaService.getById(zonaId));
        turno.setDocente(usuarioService.getById(docenteId));
        turno.setFecha(LocalDate.parse((String) body.get("date")));
        turno.setHora_inicio(LocalTime.parse((String) body.get("startTime")));
        turno.setHora_fin(LocalTime.parse((String) body.get("endTime")));
        turno.setEstado("PENDIENTE");
        turno.setLimpieza_calificacion(0);

        Turno saved = turnoService.create(turno);
        return ResponseEntity.status(201).body(shiftToMap(saved));
    }

    @DeleteMapping("/shifts/{id}")
    public ResponseEntity<Void> deleteShift(@PathVariable Long id) {
        turnoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private Map<String, Object> shiftToMap(Turno t) {
        String status = switch (t.getEstado().toUpperCase()) {
            case "ACTIVO"     -> "active";
            case "COMPLETADO" -> "completed";
            case "AUSENTE"    -> "missed";
            default           -> "pending";
        };

        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id",          String.valueOf(t.getId_turno()));
        m.put("teacherId",   String.valueOf(t.getDocente().getId_usuario()));
        m.put("teacherName", t.getDocente().getNombre());
        m.put("zoneId",      String.valueOf(t.getZona().getId_zona()));
        m.put("zoneName",    t.getZona().getNombre());
        m.put("date",        t.getFecha().toString());
        m.put("startTime",   t.getHora_inicio().format(DateTimeFormatter.ofPattern("HH:mm")));
        m.put("endTime",     t.getHora_fin().format(DateTimeFormatter.ofPattern("HH:mm")));
        m.put("status",      status);
        return m;
    }

    // ─────────────────────────────────────────
    // CONFIG  →  /api/admin/config
    // ─────────────────────────────────────────

    @GetMapping("/config")
    public ResponseEntity<Map<String, Object>> getConfig() {
        return ResponseEntity.ok(buildDefaultConfig());
    }

    @PutMapping("/config")
    public ResponseEntity<Map<String, Object>> updateConfig(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(body);
    }

    @PostMapping("/config/reset")
    public ResponseEntity<Map<String, Object>> resetConfig() {
        return ResponseEntity.ok(buildDefaultConfig());
    }

    private Map<String, Object> buildDefaultConfig() {
        Map<String, Object> c = new LinkedHashMap<>();
        c.put("shiftDuration",       30);
        c.put("minPatrols",          2);
        c.put("notificationMinutes", 15);
        c.put("autoReassignMinutes", 10);
        c.put("gamificationEnabled", true);
        c.put("pointsPerShift",      100);
        c.put("pointsPerPatrol",     25);
        c.put("pointsPerReport",     50);
        return c;
    }

    // ─────────────────────────────────────────
    // REPORTS  →  /api/admin/audit-logs, incidents, leaderboard
    // ─────────────────────────────────────────

    @GetMapping("/audit-logs")
    public ResponseEntity<List<Object>> getAuditLogs() {
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/incidents")
    public ResponseEntity<List<Object>> getIncidents() {
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<List<Object>> getLeaderboard() {
        return ResponseEntity.ok(List.of());
    }
}