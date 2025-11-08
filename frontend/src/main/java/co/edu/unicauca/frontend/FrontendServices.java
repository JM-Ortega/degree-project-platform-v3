package co.edu.unicauca.frontend;

import co.edu.unicauca.frontend.infra.config.AppConfig;
import co.edu.unicauca.frontend.infra.http.HttpAuthApi;
import co.edu.unicauca.frontend.infra.http.HttpDepartmentHeadApi;
import co.edu.unicauca.frontend.services.DocenteService;
import co.edu.unicauca.frontend.services.EstudianteService;
import co.edu.unicauca.frontend.services.ProyectoService;
import co.edu.unicauca.frontend.services.auth.AuthApi;
import co.edu.unicauca.frontend.services.auth.AuthServiceFront;
import co.edu.unicauca.frontend.services.coordinator.CoordinadorClient;
import co.edu.unicauca.frontend.services.coordinator.FormatoService;
import co.edu.unicauca.frontend.services.departmenthead.DepartmentHeadServiceFront;

public final class FrontendServices {

    private static volatile boolean initialized = false;
    private static String baseUrl;

    private static AuthServiceFront authService;
    private static DepartmentHeadServiceFront departmentHeadService;

    // nuevos
    private static CoordinadorClient coordinadorClient;
    private static FormatoService formatoService;

    private static DocenteService docenteService;
    private static EstudianteService estudianteService;
    private static ProyectoService proyectoService;

    private FrontendServices() { }

    /**
     * Llamar una sola vez en FrontendApp.start() antes de cargar FXML.
     */
    public static synchronized void init() {
        if (initialized) return;

        baseUrl = AppConfig.get("api.base-url");
        if (baseUrl == null || baseUrl.isBlank()) {
            System.err.println("[WARN] 'api.base-url' no encontrado. Usando http://localhost:8080/api");
            baseUrl = "http://localhost:8080/api";
        }

        // ===== Auth =====
        String registerEndpoint = AppConfig.get("api.endpoint.register");
        String loginEndpoint = AppConfig.get("api.endpoint.login");
        AuthApi authApi = new HttpAuthApi(baseUrl, registerEndpoint, loginEndpoint);
        authService = new AuthServiceFront(authApi);

        // ===== Department Head =====
        String sinEvaluadores = AppConfig.get("api.endpoint.sin-evaluadores");
        String buscar = AppConfig.get("api.endpoint.buscar");
        var departmentHeadApi = new HttpDepartmentHeadApi(baseUrl, sinEvaluadores, buscar);
        departmentHeadService = new DepartmentHeadServiceFront(departmentHeadApi);

        // ===== Coordinator (inyecta baseUrl si tus clientes lo requieren) =====
        coordinadorClient = new CoordinadorClient(); // o new CoordinadorClient(baseUrl)
        formatoService = new FormatoService();    // o new FormatoService(baseUrl)

        // ===== Dominio local =====
        docenteService = new DocenteService();
        estudianteService = new EstudianteService();
        proyectoService = new ProyectoService(docenteService, estudianteService);

        initialized = true;

        System.out.println("[INFO] FrontendServices inicializado con baseUrl: " + baseUrl);
        System.out.println("[INFO] sin-evaluadores: " + sinEvaluadores + " | buscar: " + buscar);
    }

    public static String baseUrl() {
        ensureInit();
        return baseUrl; }

    public static AuthServiceFront authService() {
        ensureInit();
        return authService;
    }

    public static DepartmentHeadServiceFront departmentHeadService() {
        ensureInit();
        return departmentHeadService;
    }

    public static CoordinadorClient coordinadorClient() {
        ensureInit();
        return coordinadorClient;
    }

    public static FormatoService formatoService() {
        ensureInit();
        return formatoService;
    }

    public static DocenteService docenteService() {
        ensureInit();
        return docenteService;
    }

    public static EstudianteService estudianteService() {
        ensureInit();
        return estudianteService;
    }

    public static ProyectoService proyectoService() {
        ensureInit();
        return proyectoService;
    }

    private static void ensureInit() {
        if (!initialized)
            throw new IllegalStateException("FrontendServices no ha sido inicializado. Llama init() antes.");
    }
}
