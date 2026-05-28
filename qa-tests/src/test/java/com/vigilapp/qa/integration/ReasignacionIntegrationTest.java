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
class ReasignacionIntegrationTest extends BaseIntegrationTest {

    private static String coordinadorToken;
    private static Long reasignacionId;
    private static Long turnoId;

    @BeforeAll
    static void setupTokens() {
        coordinadorToken = authenticate("coordinador@ejemplo.com", "coordinador123");
    }

    @Test
    @Order(1)
    @DisplayName("POST /api/reasignaciones → 201 Created con payload válido")
    void postCreateReasignacion_ShouldReturn201() {
        Map<String, Object> turnoPayload = Map.of(
            "fecha", "2026-05-26",
            "hora_inicio", "14:00:00",
            "hora_fin", "18:00:00",
            "estado", "ASIGNADO",
            "limpieza_calificacion", 0,
            "docente", Map.of("id_usuario", 3),
            "zona", Map.of("id_zona", 2)
        );
        Response turnoResponse = createTurno(coordinadorToken, turnoPayload);
        turnoResponse.then().statusCode(201);
        turnoId = turnoResponse.jsonPath().getLong("id_turno");
        assertNotNull(turnoId, "id_turno no debe ser nulo");

        // docenteOriginal = 3 (PROFESOR), docentePropuesto = 2 (COORDINADOR como sustituto)
        Map<String, Object> payload = Map.of(
            "motivo", "Compromiso académico imprevisto — necesito cubrir examen departamental",
            "fecha_propuesta", "2026-05-26T07:00:00",
            "estado", "PENDIENTE",
            "turno", Map.of("id_turno", turnoId),
            "docenteOriginal", Map.of("id_usuario", 3),
            "docentePropuesto", Map.of("id_usuario", 2)
        );

        Response response = given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + coordinadorToken)
            .body(payload)
            .when()
            .post(backendBaseUrl + "/api/reasignaciones");

        response.then().statusCode(201);
        reasignacionId = response.jsonPath().getLong("id_reasignacion");
        assertNotNull(reasignacionId, "id_reasignacion no debe ser nulo tras la creación");
        assertEquals("PENDIENTE", response.jsonPath().getString("estado"));
        assertEquals(
            "Compromiso académico imprevisto — necesito cubrir examen departamental",
            response.jsonPath().getString("motivo")
        );
    }

    @Test
    @Order(2)
    @DisplayName("GET /api/reasignaciones → lista contiene la reasignación creada")
    void getListReasignaciones_ShouldContainCreated() {
        assertNotNull(reasignacionId, "Este test depende de postCreateReasignacion_ShouldReturn201");

        Response response = given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + coordinadorToken)
            .when()
            .get(backendBaseUrl + "/api/reasignaciones");

        response.then().statusCode(200);
        List<Map<String, Object>> reasignaciones = response.jsonPath().getList("$");
        assertFalse(reasignaciones.isEmpty(), "La lista no debe estar vacía");

        boolean found = reasignaciones.stream()
            .anyMatch(r -> {
                Object id = r.get("id_reasignacion");
                return id != null && reasignacionId.equals(Long.valueOf(id.toString()));
            });
        assertTrue(found, "La reasignación creada debe aparecer en la lista");
    }

    @Test
    @Order(3)
    @DisplayName("PUT /api/reasignaciones/{id} → 200 OK al cambiar estado a APROBADA")
    void putApproveReasignacion_ShouldReturn200WithApprovedState() {
        assertNotNull(reasignacionId, "Este test depende de postCreateReasignacion_ShouldReturn201");

        Map<String, Object> updatedPayload = Map.of(
            "motivo", "Compromiso académico imprevisto — necesito cubrir examen departamental",
            "fecha_propuesta", "2026-05-26T07:00:00",
            "fecha_respuesta", "2026-05-26T08:30:00",
            "estado", "APROBADA",
            "turno", Map.of("id_turno", turnoId),
            "docenteOriginal", Map.of("id_usuario", 3),
            "docentePropuesto", Map.of("id_usuario", 2)
        );

        Response response = given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + coordinadorToken)
            .body(updatedPayload)
            .when()
            .put(backendBaseUrl + "/api/reasignaciones/" + reasignacionId);

        response.then().statusCode(200);
        assertEquals("APROBADA", response.jsonPath().getString("estado"),
            "El estado debe cambiar de PENDIENTE a APROBADA");
        assertNotNull(response.jsonPath().getString("fecha_respuesta"),
            "La fecha de respuesta debe estar presente tras la aprobación");
    }

    @Test
    @Order(4)
    @DisplayName("DELETE /api/reasignaciones/{id} → 204, luego GET → 404")
    void deleteReasignacion_ThenGetShouldReturn404() {
        assertNotNull(reasignacionId, "Este test depende de postCreateReasignacion_ShouldReturn201");

        given()
            .header("Authorization", "Bearer " + coordinadorToken)
            .when()
            .delete(backendBaseUrl + "/api/reasignaciones/" + reasignacionId)
            .then()
            .statusCode(204);

        given()
            .header("Authorization", "Bearer " + coordinadorToken)
            .when()
            .get(backendBaseUrl + "/api/reasignaciones/" + reasignacionId)
            .then()
            .statusCode(404);
    }
}
