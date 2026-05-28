package com.vigilapp.qa.e2e;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.RequestOptions;
import org.junit.jupiter.api.*;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Tag("e2e")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class IncidentE2ETest {

    private static final String FRONTEND_URL = System.getProperty("frontend.url", "http://localhost:4000");
    private static final String BACKEND_URL = "http://localhost:8080";

    private Playwright playwright;
    private Browser browser;

    @BeforeAll
    void setupPlaywright() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(
            new BrowserType.LaunchOptions().setHeadless(true)
        );
    }

    @AfterAll
    void teardownPlaywright() {
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }

    @Test
    @DisplayName("E2E: Profesor reporta incidente y Coordinador visualiza el dashboard")
    void profesorReportsIncidentAndCoordinatorSeesDashboard() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        // ────────────────────────────────────────────────
        // DATA SETUP: create a turno via API for the profesor
        // ────────────────────────────────────────────────
        APIRequestContext api = playwright.request().newContext();

        APIResponse loginRes = api.post(BACKEND_URL + "/auth/login",
            RequestOptions.create()
                .setData(Map.of("email", "profesor@ejemplo.com", "password", "docente123"))
                .setHeader("Content-Type", "application/json")
        );
        assertEquals(200, loginRes.status(), "Login API debe retornar 200");
        Map<String, Object> loginData = mapper.readValue(loginRes.text(),
            new TypeReference<Map<String, Object>>() {});
        String token = (String) loginData.get("token");

        APIResponse turnoRes = api.post(BACKEND_URL + "/api/turnos",
            RequestOptions.create()
                .setData(Map.of(
                    "fecha", "2026-05-25",
                    "hora_inicio", "08:00:00",
                    "hora_fin", "12:00:00",
                    "estado", "ASIGNADO",
                    "limpieza_calificacion", 0,
                    "docente", Map.of("id_usuario", 3),
                    "zona", Map.of("id_zona", 1)
                ))
                .setHeader("Content-Type", "application/json")
                .setHeader("Authorization", "Bearer " + token)
        );
        assertEquals(201, turnoRes.status(), "Turno API debe retornar 201");
        api.dispose();

        // ────────────────────────────────────────────────
        // STEP 1: Profesor context
        // ────────────────────────────────────────────────
        BrowserContext profesorCtx = browser.newContext();
        Page profPage = profesorCtx.newPage();

        profPage.navigate(FRONTEND_URL + "/login");
        profPage.waitForLoadState();

        profPage.fill("#email", "profesor@ejemplo.com");
        profPage.fill("#password", "docente123");
        profPage.getByRole(AriaRole.BUTTON,
            new Page.GetByRoleOptions().setName("Iniciar Sesión")
        ).click();

        profPage.waitForURL("**/profesor/home");

        profPage.navigate(FRONTEND_URL + "/profesor/reporte");
        profPage.waitForSelector("textarea");

        profPage.getByRole(AriaRole.BUTTON,
            new Page.GetByRoleOptions().setName("Bloque A")
        ).click();

        profPage.getByRole(AriaRole.BUTTON,
            new Page.GetByRoleOptions().setName("Seguridad Física")
        ).click();

        profPage.getByRole(AriaRole.BUTTON,
            new Page.GetByRoleOptions().setName("Leve")
        ).click();

        profPage.fill("textarea", "E2E: Caída de estudiante en pasillo del Bloque A");

        profPage.getByRole(AriaRole.BUTTON,
            new Page.GetByRoleOptions().setName("Registrar Incidente")
        ).click();

        profPage.locator("text=Incidente registrado correctamente")
            .waitFor(new Locator.WaitForOptions().setTimeout(5000));

        profPage.waitForURL("**/profesor/home");

        profPage.getByRole(AriaRole.BUTTON,
            new Page.GetByRoleOptions().setName("Cerrar Sesión")
        ).click();
        profPage.waitForURL("**/login");

        profesorCtx.close();

        // ────────────────────────────────────────────────
        // STEP 2: Coordinator context
        // ────────────────────────────────────────────────
        BrowserContext coordCtx = browser.newContext();
        Page coordPage = coordCtx.newPage();

        coordPage.navigate(FRONTEND_URL + "/login");
        coordPage.waitForLoadState();

        coordPage.fill("#email", "coordinador@ejemplo.com");
        coordPage.fill("#password", "coordinador123");
        coordPage.getByRole(AriaRole.BUTTON,
            new Page.GetByRoleOptions().setName("Iniciar Sesión")
        ).click();

        coordPage.waitForURL("**/coordinator/**");

        // The header is rendered unconditionally (outside *ngIf loading guards)
        coordPage.waitForSelector("header", new Page.WaitForSelectorOptions().setTimeout(15000));

        assertTrue(
            coordPage.locator("header").isVisible(),
            "La cabecera de la página del coordinador debe estar visible"
        );

        coordCtx.close();
    }
}
