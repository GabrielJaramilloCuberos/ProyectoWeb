package com.vigilapp.qa.infrastructure;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;

import java.util.List;
import java.util.Map;

@Tag("integration")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Testcontainers
public abstract class BaseIntegrationTest {

    private static final int BACKEND_PORT = 8080;

    private static final PostgreSQLContainer<?> postgres =
        new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("vigilapp_db")
            .withCopyFileToContainer(
                MountableFile.forClasspathResource("init.sql"),
                "/docker-entrypoint-initdb.d/init.sql"
            );

    private static final GenericContainer<?> backend =
        new GenericContainer<>("vigilapp-backend:latest")
            .withExposedPorts(BACKEND_PORT)
            .withEnv("SPRING_JPA_HIBERNATE_DDL_AUTO", "update")
            .withEnv("SPRING_JPA_SHOW_SQL", "false")
            .withEnv("PORT", String.valueOf(BACKEND_PORT))
            .waitingFor(Wait.forHttp("/actuator/health").forStatusCode(200))
            .withStartupTimeout(java.time.Duration.ofMinutes(5));

    protected static String backendBaseUrl;

    protected static Long zonaId;
    protected static String zonaNombre;
    protected static Long tipoId;
    protected static String tipoNombre;
    protected static Long severidadId;
    protected static String severidadCodigo;
    protected static Long profesorUserId;

    @BeforeAll
    void startInfrastructure() {
        postgres.start();

        backend.withEnv("SPRING_DATASOURCE_URL",
            "jdbc:postgresql://" + postgres.getHost() + ":" + postgres.getMappedPort(5432) + "/vigilapp_db");
        backend.withEnv("DB_USER", postgres.getUsername());
        backend.withEnv("DB_PASSWORD", postgres.getPassword());
        backend.start();

        backendBaseUrl = "http://" + backend.getHost() + ":" + backend.getMappedPort(BACKEND_PORT);
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @AfterAll
    void stopInfrastructure() {
        try {
            if (backend != null && backend.isRunning()) {
                backend.stop();
            }
        } finally {
            if (postgres != null && postgres.isRunning()) {
                postgres.stop();
            }
        }
    }

    protected static String authenticate(String email, String password) {
        Response response = RestAssured
            .given()
            .contentType(ContentType.JSON)
            .body(Map.of("email", email, "password", password))
            .when()
            .post(backendBaseUrl + "/auth/login");

        response.then().statusCode(200);
        return response.jsonPath().getString("token");
    }

    protected static Response apiGet(String token, String path) {
        return RestAssured
            .given()
            .header("Authorization", "Bearer " + token)
            .when()
            .get(backendBaseUrl + path);
    }

    protected static Response createTurno(String token, Object turnoPayload) {
        return RestAssured
            .given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + token)
            .body(turnoPayload)
            .when()
            .post(backendBaseUrl + "/api/turnos");
    }

    protected static Response createIncidente(String token, Object incidentePayload) {
        return RestAssured
            .given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + token)
            .body(incidentePayload)
            .when()
            .post(backendBaseUrl + "/api/incidentes");
    }

    protected static void fetchCatalogs(String token) {
        Response zonasResp = apiGet(token, "/api/zonas");
        zonasResp.then().statusCode(200);
        List<Object> zonas = zonasResp.jsonPath().getList("$");
        if (zonas != null && !zonas.isEmpty()) {
            zonaId = zonasResp.jsonPath().getLong("[0].id_zona");
            zonaNombre = zonasResp.jsonPath().getString("[0].nombre");
        }

        Response tiposResp = apiGet(token, "/api/tipos-incidente");
        tiposResp.then().statusCode(200);
        List<Object> tipos = tiposResp.jsonPath().getList("$");
        if (tipos != null && !tipos.isEmpty()) {
            tipoId = tiposResp.jsonPath().getLong("[0].id_tipo");
            tipoNombre = tiposResp.jsonPath().getString("[0].nombre");
        }

        Response sevResp = apiGet(token, "/api/severidades");
        sevResp.then().statusCode(200);
        List<Object> sevs = sevResp.jsonPath().getList("$");
        if (sevs != null && !sevs.isEmpty()) {
            severidadId = sevResp.jsonPath().getLong("[0].id_severidad");
            severidadCodigo = sevResp.jsonPath().getString("[0].codigo");
        }

        Response usuariosResp = apiGet(token, "/api/usuarios");
        usuariosResp.then().statusCode(200);
        profesorUserId = usuariosResp.jsonPath()
            .getLong("find { it.rol.nombre == 'PROFESOR' }.id_usuario");
    }
}
