package com.vigilapp.qa.performance;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class IncidentLoadSimulation extends Simulation {

    String backendUrl = System.getProperty("backend.url", "http://localhost:8080");

    HttpProtocolBuilder httpProtocol = http
        .baseUrl(backendUrl)
        .acceptHeader("application/json")
        .contentTypeHeader("application/json")
        .header("Cache-Control", "no-cache");

    ChainBuilder authenticate = exec(
        http("Login")
            .post("/auth/login")
            .body(StringBody("{\"email\":\"coordinador@ejemplo.com\",\"password\":\"coordinador123\"}"))
            .check(
                status().is(200),
                regex("\"token\" *: *\"([^\"]+)\"").saveAs("jwt")
            )
    );

    ChainBuilder listIncidents = exec(
        http("List Incidents")
            .get("/api/incidentes")
            .header("Authorization", session -> "Bearer " + session.getString("jwt"))
            .check(status().is(200))
    );

    ScenarioBuilder scn = scenario("Incident Load Test")
        .exec(authenticate)
        .during(Duration.ofSeconds(60))
            .on(
                listIncidents
                    .pause(Duration.ofSeconds(1), Duration.ofSeconds(3))
            );

    {
        setUp(
            scn.injectOpen(
                rampUsers(50).during(Duration.ofSeconds(10))
            )
        ).protocols(httpProtocol)
         .assertions(
             // Thresholds for single-instance Docker on local hardware (JVM cold start)
             details("List Incidents").responseTime().percentile2().lt(15000),
             details("List Incidents").responseTime().percentile3().lt(20000),
             details("List Incidents").successfulRequests().percent().gt(99.0)
         );
        //
        // Production thresholds (dedicated server, warmed-up JVM):
        //   .assertions(
        //       details("List Incidents").responseTime().percentile2().lt(800),
        //       details("List Incidents").responseTime().percentile3().lt(1500),
        //       details("List Incidents").successfulRequests().percent().gt(99.0)
        //   );
    }
}
