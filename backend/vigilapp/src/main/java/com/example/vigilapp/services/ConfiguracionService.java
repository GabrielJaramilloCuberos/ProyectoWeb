package com.example.vigilapp.services;

import com.example.vigilapp.entities.Configuracion;
import com.example.vigilapp.repositories.ConfiguracionRepository;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ConfiguracionService {

    private final ConfiguracionRepository repo;

    public ConfiguracionService(ConfiguracionRepository repo) {
        this.repo = repo;
    }

    public Configuracion getConfig() {
        return repo.findAll().stream().findFirst().orElseGet(this::createDefault);
    }

    public Configuracion updateConfig(Map<String, Object> body) {
        Configuracion config = getConfig();
        applyBody(config, body);
        return repo.save(config);
    }

    public Configuracion resetConfig() {
        Configuracion config = getConfig();
        applyDefaults(config);
        return repo.save(config);
    }

    private Configuracion createDefault() {
        Configuracion c = new Configuracion();
        applyDefaults(c);
        return repo.save(c);
    }

    private void applyDefaults(Configuracion c) {
        c.setShiftDuration(30);
        c.setMinPatrols(2);
        c.setNotificationMinutes(15);
        c.setAutoReassignMinutes(10);
        c.setGamificationEnabled(true);
        c.setPointsPerShift(100);
        c.setPointsPerPatrol(25);
        c.setPointsPerReport(50);
    }

    private void applyBody(Configuracion c, Map<String, Object> body) {
        if (body.containsKey("shiftDuration"))       c.setShiftDuration(toInt(body.get("shiftDuration")));
        if (body.containsKey("minPatrols"))          c.setMinPatrols(toInt(body.get("minPatrols")));
        if (body.containsKey("notificationMinutes")) c.setNotificationMinutes(toInt(body.get("notificationMinutes")));
        if (body.containsKey("autoReassignMinutes")) c.setAutoReassignMinutes(toInt(body.get("autoReassignMinutes")));
        if (body.containsKey("gamificationEnabled")) c.setGamificationEnabled((Boolean) body.get("gamificationEnabled"));
        if (body.containsKey("pointsPerShift"))      c.setPointsPerShift(toInt(body.get("pointsPerShift")));
        if (body.containsKey("pointsPerPatrol"))     c.setPointsPerPatrol(toInt(body.get("pointsPerPatrol")));
        if (body.containsKey("pointsPerReport"))     c.setPointsPerReport(toInt(body.get("pointsPerReport")));
    }

    private Integer toInt(Object val) {
        if (val == null) return null;
        return val instanceof Number ? ((Number) val).intValue() : Integer.parseInt(val.toString());
    }

    public Map<String, Object> toMap(Configuracion c) {
        return Map.of(
            "shiftDuration",       c.getShiftDuration(),
            "minPatrols",          c.getMinPatrols(),
            "notificationMinutes", c.getNotificationMinutes(),
            "autoReassignMinutes", c.getAutoReassignMinutes(),
            "gamificationEnabled", c.getGamificationEnabled(),
            "pointsPerShift",      c.getPointsPerShift(),
            "pointsPerPatrol",     c.getPointsPerPatrol(),
            "pointsPerReport",     c.getPointsPerReport()
        );
    }
}
