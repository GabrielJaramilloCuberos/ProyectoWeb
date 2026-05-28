package com.vigilapp.qa.integration;

import com.vigilapp.qa.infrastructure.BaseIntegrationTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class IncidentIntegrationTest extends BaseIntegrationTest {

    private static String profesorToken;
    private static String coordinadorToken;
    private static Long turnoId;
    private static Long incidentId;

    @BeforeAll
    static void setupTokens() {
        profesorToken = authenticate("profesor@ejemplo.com", "docente123");
        coordinadorToken = authenticate("coordinador@ejemplo.com", "coordinador123");
    }

    @Test
    @Order(1)
    @DisplayName("POST /api/incidentes → 201 Created cuando el payload es válido")
    void postCreateIncident_ShouldReturn201() {
        Map<String, Object> turnoPayload = Map.of(
            "fecha", "2026-05-25",
            "hora_inicio", "08:00:00",
            "hora_fin", "12:00:00",
            "estado", "ASIGNADO",
            "limpieza_calificacion", 0,
            "docente", Map.of("id_usuario", 3),
            "zona", Map.of("id_zona", 1)
        );
        Response turnoResponse = createTurno(profesorToken, turnoPayload);
        turnoResponse.then().statusCode(201);
        turnoId = turnoResponse.jsonPath().getLong("id_turno");
        assertNotNull(turnoId, "id_turno no debe ser nulo tras la creación");

        Map<String, Object> incidentePayload = Map.of(
            "fecha_hora", "2026-05-25T10:00:00",
            "descripcion", "Incidente de prueba - Caída en pasillo principal",
            "turno", Map.of("id_turno", turnoId),
            "zona", Map.of("id_zona", 1),
            "tipoIncidente", Map.of("id_tipo", 1),
            "severidad", Map.of("id_severidad", 1)
        );
        Response incidenteResponse = createIncidente(profesorToken, incidentePayload);
        incidenteResponse.then().statusCode(201);

        incidentId = incidenteResponse.jsonPath().getLong("id_incidente");
        assertNotNull(incidentId, "id_incidente no debe ser nulo tras la creación");
        assertEquals("Incidente de prueba - Caída en pasillo principal",
            incidenteResponse.jsonPath().getString("descripcion"));
    }

    @Test
    @Order(2)
    @DisplayName("GET /api/incidentes → contiene el incidente recién creado")
    void getListIncidents_ShouldContainCreated() {
        assertNotNull(incidentId, "Este test depende de postCreateIncident_ShouldReturn201");

        Response response = given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + coordinadorToken)
            .when()
            .get(backendBaseUrl + "/api/incidentes");

        response.then().statusCode(200);
        List<Map<String, Object>> incidentes = response.jsonPath().getList("$");
        assertFalse(incidentes.isEmpty(), "La lista de incidentes no debe estar vacía");

        boolean found = incidentes.stream()
            .anyMatch(i -> {
                Object id = i.get("id_incidente");
                return id != null && incidentId.equals(Long.valueOf(id.toString()));
            });
        assertTrue(found, "El incidente creado debe aparecer en la lista global");
    }

    @Test
    @Order(3)
    @DisplayName("PUT /api/incidentes/{id} → 200 OK al reasignar el turno")
    void putReassignIncident_ShouldReturn200() {
        assertNotNull(incidentId, "Este test depende de postCreateIncident_ShouldReturn201");

        Map<String, Object> segundoTurno = Map.of(
            "fecha", "2026-05-25",
            "hora_inicio", "14:00:00",
            "hora_fin", "18:00:00",
            "estado", "ASIGNADO",
            "limpieza_calificacion", 0,
            "docente", Map.of("id_usuario", 3),
            "zona", Map.of("id_zona", 2)
        );
        Response turnoResponse = createTurno(profesorToken, segundoTurno);
        turnoResponse.then().statusCode(201);
        Long nuevoTurnoId = turnoResponse.jsonPath().getLong("id_turno");

        Map<String, Object> incidenteActualizado = Map.of(
            "fecha_hora", "2026-05-25T10:00:00",
            "descripcion", "Incidente de prueba - Caída en pasillo principal (reasignado)",
            "turno", Map.of("id_turno", nuevoTurnoId),
            "zona", Map.of("id_zona", 1),
            "tipoIncidente", Map.of("id_tipo", 1),
            "severidad", Map.of("id_severidad", 1)
        );
        Response response = given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + coordinadorToken)
            .body(incidenteActualizado)
            .when()
            .put(backendBaseUrl + "/api/incidentes/" + incidentId);

        response.then().statusCode(200);
        Long turnoReasignado = response.jsonPath().getLong("turno.id_turno");
        assertEquals(nuevoTurnoId, turnoReasignado,
            "El id_turno debe reflejar la reasignación");
    }

    @Test
    @Order(4)
    @DisplayName("DELETE /api/incidentes/{id} → 204, luego GET → 404")
    void deleteIncident_ThenGetShouldReturn404() {
        assertNotNull(incidentId, "Este test depende de postCreateIncident_ShouldReturn201");

        given()
            .header("Authorization", "Bearer " + coordinadorToken)
            .when()
            .delete(backendBaseUrl + "/api/incidentes/" + incidentId)
            .then()
            .statusCode(204);

        given()
            .header("Authorization", "Bearer " + coordinadorToken)
            .when()
            .get(backendBaseUrl + "/api/incidentes/" + incidentId)
            .then()
            .statusCode(404);
    }
}
