package com.vigilapp.qa.e2e;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.RequestOptions;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Tag("e2e")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class IncidentE2ETest {

    private static final String FRONTEND_URL = System.getProperty("frontend.url", "http://localhost:4000");
    private static final String BACKEND_URL = "http://localhost:8080";
    private static final String PROFESOR_EMAIL = "profesor@ejemplo.com";
    private static final String PROFESOR_PASSWORD = "docente123";
    private static final String COORDINADOR_EMAIL = "coordinador@ejemplo.com";
    private static final String COORDINADOR_PASSWORD = "coordinador123";

    private static Long zonaId;
    private static String zonaNombre;
    private static String tipoNombre;
    private static String severidadCodigo;
    private static Long profesorUserId;

    private Playwright playwright;
    private Browser browser;
    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeAll
    void setupPlaywrightAndCatalogs() throws Exception {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(
            new BrowserType.LaunchOptions().setHeadless(true)
        );
        fetchCatalogsFromApi();
    }

    @AfterAll
    void teardownPlaywright() {
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }

    private void fetchCatalogsFromApi() throws Exception {
        APIRequestContext api = playwright.request().newContext();

        APIResponse loginRes = api.post(BACKEND_URL + "/auth/login",
            RequestOptions.create()
                .setData(Map.of("email", COORDINADOR_EMAIL, "password", COORDINADOR_PASSWORD))
                .setHeader("Content-Type", "application/json")
        );
        assertEquals(200, loginRes.status(), "Login API debe retornar 200 para fetch de catálogos");
        Map<String, Object> loginData = mapper.readValue(loginRes.text(),
            new TypeReference<Map<String, Object>>() {});
        String token = (String) loginData.get("token");

        APIResponse zonasResp = api.get(BACKEND_URL + "/api/zonas",
            RequestOptions.create().setHeader("Authorization", "Bearer " + token));
        List<Map<String, Object>> zonas = mapper.readValue(zonasResp.text(),
            new TypeReference<List<Map<String, Object>>>() {});
        assertFalse(zonas.isEmpty(), "Debe haber al menos una zona en el catálogo");
        zonaId = ((Number) zonas.get(0).get("id_zona")).longValue();
        zonaNombre = (String) zonas.get(0).get("nombre");

        APIResponse tiposResp = api.get(BACKEND_URL + "/api/tipos-incidente",
            RequestOptions.create().setHeader("Authorization", "Bearer " + token));
        List<Map<String, Object>> tipos = mapper.readValue(tiposResp.text(),
            new TypeReference<List<Map<String, Object>>>() {});
        assertFalse(tipos.isEmpty(), "Debe haber al menos un tipo de incidente");
        tipoNombre = (String) tipos.get(0).get("nombre");

        APIResponse sevResp = api.get(BACKEND_URL + "/api/severidades",
            RequestOptions.create().setHeader("Authorization", "Bearer " + token));
        List<Map<String, Object>> sevs = mapper.readValue(sevResp.text(),
            new TypeReference<List<Map<String, Object>>>() {});
        assertFalse(sevs.isEmpty(), "Debe haber al menos una severidad");
        severidadCodigo = (String) sevs.get(0).get("codigo");

        APIResponse userResp = api.get(BACKEND_URL + "/api/usuarios",
            RequestOptions.create().setHeader("Authorization", "Bearer " + token));
        List<Map<String, Object>> usuarios = mapper.readValue(userResp.text(),
            new TypeReference<List<Map<String, Object>>>() {});
        profesorUserId = usuarios.stream()
            .filter(u -> {
                Map<String, Object> rol = (Map<String, Object>) u.get("rol");
                return "PROFESOR".equals(rol.get("nombre"));
            })
            .map(u -> ((Number) u.get("id_usuario")).longValue())
            .findFirst()
            .orElseThrow(() -> new AssertionError("No se encontró usuario con rol PROFESOR"));

        api.dispose();
    }

    private String generateDescription() {
        return "E2E-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }

    private boolean incidentExistsInApi(String description) throws Exception {
        APIRequestContext api = playwright.request().newContext();

        APIResponse loginRes = api.post(BACKEND_URL + "/auth/login",
            RequestOptions.create()
                .setData(Map.of("email", COORDINADOR_EMAIL, "password", COORDINADOR_PASSWORD))
                .setHeader("Content-Type", "application/json")
        );
        Map<String, Object> loginData = mapper.readValue(loginRes.text(),
            new TypeReference<Map<String, Object>>() {});
        String token = (String) loginData.get("token");

        APIResponse listRes = api.get(BACKEND_URL + "/api/incidentes",
            RequestOptions.create().setHeader("Authorization", "Bearer " + token));
        List<Map<String, Object>> incidentes = mapper.readValue(listRes.text(),
            new TypeReference<List<Map<String, Object>>>() {});
        api.dispose();

        return incidentes.stream()
            .anyMatch(i -> description.equals(i.get("descripcion")));
    }

    @Test
    @DisplayName("E2E: Profesor reporta incidente y Coordinador verifica vía API + UI")
    void profesorReportsIncidentAndCoordinatorVerifies() throws Exception {
        String descripcion = generateDescription();

        assertNotNull(zonaId, "zonaId debe haberse obtenido del catálogo");
        assertNotNull(zonaNombre, "zonaNombre debe haberse obtenido del catálogo");
        assertNotNull(tipoNombre, "tipoNombre debe haberse obtenido del catálogo");
        assertNotNull(severidadCodigo, "severidadCodigo debe haberse obtenido del catálogo");
        assertNotNull(profesorUserId, "profesorUserId debe haberse obtenido del catálogo");

        APIRequestContext api = playwright.request().newContext();

        APIResponse loginRes = api.post(BACKEND_URL + "/auth/login",
            RequestOptions.create()
                .setData(Map.of("email", PROFESOR_EMAIL, "password", PROFESOR_PASSWORD))
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
                    "docente", Map.of("id_usuario", profesorUserId),
                    "zona", Map.of("id_zona", zonaId)
                ))
                .setHeader("Content-Type", "application/json")
                .setHeader("Authorization", "Bearer " + token)
        );
        assertEquals(201, turnoRes.status(), "Turno API debe retornar 201");
        api.dispose();

        BrowserContext profesorCtx = browser.newContext();
        Page profPage = profesorCtx.newPage();

        profPage.navigate(FRONTEND_URL + "/login");
        profPage.waitForLoadState();

        profPage.fill("#email", PROFESOR_EMAIL);
        profPage.fill("#password", PROFESOR_PASSWORD);
        profPage.getByRole(AriaRole.BUTTON,
            new Page.GetByRoleOptions().setName("Iniciar Sesión")
        ).click();

        profPage.waitForURL("**/profesor/home");

        profPage.navigate(FRONTEND_URL + "/profesor/reporte");
        profPage.waitForSelector("textarea");

        profPage.getByRole(AriaRole.BUTTON,
            new Page.GetByRoleOptions().setName(zonaNombre)
        ).click();

        profPage.getByRole(AriaRole.BUTTON,
            new Page.GetByRoleOptions().setName(tipoNombre)
        ).click();

        profPage.getByRole(AriaRole.BUTTON,
            new Page.GetByRoleOptions().setName(severidadCodigo)
        ).click();

        profPage.fill("textarea", descripcion);

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

        BrowserContext coordCtx = browser.newContext();
        Page coordPage = coordCtx.newPage();

        coordPage.navigate(FRONTEND_URL + "/login");
        coordPage.waitForLoadState();

        coordPage.fill("#email", COORDINADOR_EMAIL);
        coordPage.fill("#password", COORDINADOR_PASSWORD);
        coordPage.getByRole(AriaRole.BUTTON,
            new Page.GetByRoleOptions().setName("Iniciar Sesión")
        ).click();

        coordPage.waitForURL("**/coordinator/home");

        assertTrue(
            coordPage.locator("app-coordinator-stats").isVisible(),
            "Dashboard de coordinador debe cargar con el componente de estadísticas"
        );

        assertTrue(incidentExistsInApi(descripcion),
            "El incidente '" + descripcion + "' debe existir en la API al consultar como coordinador");

        coordCtx.close();
    }
}
