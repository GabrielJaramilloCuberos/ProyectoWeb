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
class CoordinadorAnalyticsE2ETest {

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
    @DisplayName("E2E: Coordinador navega del dashboard al panel de analytics y filtra incidentes por zona")
    void coordinadorNavigatesDashboardToAnalyticsAndFilters() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        // ────────────────────────────────────────────────
        // DATA SETUP: crear turno e incidente para que analytics tenga datos
        // ────────────────────────────────────────────────
        APIRequestContext api = playwright.request().newContext();

        APIResponse loginRes = api.post(BACKEND_URL + "/auth/login",
            RequestOptions.create()
                .setData(Map.of("email", "coordinador@ejemplo.com", "password", "coordinador123"))
                .setHeader("Content-Type", "application/json")
        );
        assertEquals(200, loginRes.status(), "Login API debe retornar 200");
        Map<String, Object> loginData = mapper.readValue(loginRes.text(),
            new TypeReference<Map<String, Object>>() {});
        String coordToken = (String) loginData.get("token");

        APIResponse turnoRes = api.post(BACKEND_URL + "/api/turnos",
            RequestOptions.create()
                .setData(Map.of(
                    "fecha", "2026-05-26",
                    "hora_inicio", "08:00:00",
                    "hora_fin", "12:00:00",
                    "estado", "ASIGNADO",
                    "limpieza_calificacion", 0,
                    "docente", Map.of("id_usuario", 3),
                    "zona", Map.of("id_zona", 1)
                ))
                .setHeader("Content-Type", "application/json")
                .setHeader("Authorization", "Bearer " + coordToken)
        );
        assertEquals(201, turnoRes.status(), "Creación de turno debe retornar 201");
        Map<String, Object> turnoData = mapper.readValue(turnoRes.text(),
            new TypeReference<Map<String, Object>>() {});
        Object turnoId = turnoData.get("id_turno");

        APIResponse incidenteRes = api.post(BACKEND_URL + "/api/incidentes",
            RequestOptions.create()
                .setData(Map.of(
                    "fecha_hora", "2026-05-26T09:00:00",
                    "descripcion", "E2E Analytics: accidente menor en Bloque A",
                    "turno", Map.of("id_turno", turnoId),
                    "zona", Map.of("id_zona", 1),
                    "tipoIncidente", Map.of("id_tipo", 1),
                    "severidad", Map.of("id_severidad", 1)
                ))
                .setHeader("Content-Type", "application/json")
                .setHeader("Authorization", "Bearer " + coordToken)
        );
        assertEquals(201, incidenteRes.status(), "Creación de incidente debe retornar 201");
        api.dispose();

        // ────────────────────────────────────────────────
        // STEP 1: Coordinador inicia sesión
        // ────────────────────────────────────────────────
        BrowserContext ctx = browser.newContext();
        Page page = ctx.newPage();

        page.navigate(FRONTEND_URL + "/login");
        page.waitForLoadState();

        page.fill("#email", "coordinador@ejemplo.com");
        page.fill("#password", "coordinador123");
        page.getByRole(AriaRole.BUTTON,
            new Page.GetByRoleOptions().setName("Iniciar Sesión")
        ).click();

        // Login navigates directly to /coordinator/dashboard
        page.waitForURL("**/coordinator/dashboard");

        // ────────────────────────────────────────────────
        // STEP 2: Verificar acciones principales del dashboard
        // ────────────────────────────────────────────────
        // Wait for the action cards to render (forkJoin with 4 APIs must complete)
        page.waitForSelector("h3:has-text('Monitoreo en Vivo')", new Page.WaitForSelectorOptions().setTimeout(30000));

        Locator monitoreoBtn = page.getByRole(AriaRole.BUTTON,
            new Page.GetByRoleOptions().setName("Monitoreo en Vivo"));
        Locator reportesBtn = page.getByRole(AriaRole.BUTTON,
            new Page.GetByRoleOptions().setName("Reportes"));

        assertTrue(monitoreoBtn.isVisible(), "El botón 'Monitoreo en Vivo' debe estar visible en el dashboard");
        assertTrue(reportesBtn.isVisible(), "El botón 'Reportes' debe estar visible en el dashboard");

        // ────────────────────────────────────────────────
        // STEP 3: Navegar a Analytics y verificar carga completa
        // ────────────────────────────────────────────────
        reportesBtn.click();
        page.waitForURL("**/coordinator/analytics");

        page.waitForSelector("select", new Page.WaitForSelectorOptions().setTimeout(8000));

        assertTrue(
            page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Reportes"))
                .isVisible(),
            "La página de analytics debe mostrar el encabezado 'Reportes'"
        );
        assertTrue(
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Exportar"))
                .isVisible(),
            "El botón 'Exportar' debe estar visible en la página de analytics"
        );
        assertTrue(
            page.locator("text=Mapa de calor").isVisible(),
            "La sección 'Mapa de calor' debe estar visible"
        );
        assertTrue(
            page.locator("text=Total incidentes").isVisible(),
            "El KPI 'Total incidentes' debe estar visible"
        );

        // ────────────────────────────────────────────────
        // STEP 4: Interactuar con el filtro de zona
        // ────────────────────────────────────────────────
        // Los dos <select> son: [0] = período, [1] = zona
        Locator selects = page.locator("select");
        selects.nth(1).selectOption(new com.microsoft.playwright.options.SelectOption().setLabel("Bloque A"));

        // La página debe seguir visible y no mostrar error tras el filtro
        assertFalse(
            page.locator("text=Cargando reportes...").isVisible(),
            "No debe mostrar spinner de carga después de aplicar el filtro"
        );

        // ────────────────────────────────────────────────
        // STEP 5: Regresar al dashboard y cerrar sesión
        // ────────────────────────────────────────────────
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("←")).click();
        page.waitForURL("**/coordinator/dashboard");

        // Sign out button is in the dashboard header
        page.waitForSelector("button:has-text('Cerrar sesión')", new Page.WaitForSelectorOptions().setTimeout(10000));
        page.getByRole(AriaRole.BUTTON,
            new Page.GetByRoleOptions().setName("Cerrar sesión")
        ).click();
        page.waitForURL("**/login");

        ctx.close();
    }
}
