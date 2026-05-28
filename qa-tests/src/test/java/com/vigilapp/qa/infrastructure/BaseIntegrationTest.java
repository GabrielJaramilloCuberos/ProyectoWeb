package com.vigilapp.qa.infrastructure;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;

import java.util.Map;

@Tag("integration")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Testcontainers
public abstract class BaseIntegrationTest {

    private static final int BACKEND_PORT = 8080;
    private static final String POSTGRES_ALIAS = "postgres-db";

    private static final Network network = Network.newNetwork();

    private static final PostgreSQLContainer<?> postgres =
        new PostgreSQLContainer<>("postgres:15-alpine")
            .withNetwork(network)
            .withNetworkAliases(POSTGRES_ALIAS)
            .withDatabaseName("vigilapp_db")
            .withCopyFileToContainer(
                MountableFile.forClasspathResource("init.sql"),
                "/docker-entrypoint-initdb.d/init.sql"
            );

    private static final GenericContainer<?> backend =
        new GenericContainer<>("vigilapp-backend:latest")
            .withNetwork(network)
            .withExposedPorts(BACKEND_PORT)
            .withEnv("SPRING_JPA_HIBERNATE_DDL_AUTO", "update")
            .withEnv("SPRING_JPA_SHOW_SQL", "false")
            .withEnv("PORT", String.valueOf(BACKEND_PORT))
            // /actuator/health returns 403 (Spring Security); wait for the startup log instead
            .waitingFor(Wait.forLogMessage(".*Started VigilappApplication.*\\n", 1))
            .withStartupTimeout(java.time.Duration.ofMinutes(5));

    protected static String backendBaseUrl;

    @BeforeAll
    void startInfrastructure() {
        postgres.start();

        // Use the internal network alias and container port so the backend container
        // can reach Postgres via the shared Docker network (localhost would resolve
        // to the backend container itself, not the host).
        backend.withEnv("SPRING_DATASOURCE_URL",
            "jdbc:postgresql://" + POSTGRES_ALIAS + ":5432/vigilapp_db");
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
}
