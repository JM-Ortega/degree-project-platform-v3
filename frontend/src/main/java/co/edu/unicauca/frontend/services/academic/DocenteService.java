package co.edu.unicauca.frontend.services.academic;

import co.edu.unicauca.frontend.infra.dto.UsuarioDTO;
import co.edu.unicauca.frontend.infra.session.SessionManager;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public class DocenteService {

    private final HttpClient http;
    private final ObjectMapper mapper;
    private final String baseUrlDocente;

    public DocenteService() {
        this("https://api.narvaezlab.dev/api/academic/docentes");
    }

    public DocenteService(String baseUrlDocente) {
        this.http = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();

        this.mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        this.baseUrlDocente = baseUrlDocente;
    }

    public UsuarioDTO obtenerDocenteActivo(String correo) {
        try {
            String url = baseUrlDocente + "/" + enc(correo);

            HttpRequest.Builder rb = HttpRequest.newBuilder(URI.create(url))
                    .timeout(Duration.ofSeconds(8));

            String token = SessionManager.getInstance().getAccessToken();
            if (token != null && !token.isBlank()) {
                rb.header("Authorization", "Bearer " + token);
            }

            HttpRequest req = rb.GET().build();

            HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
            validar2xx(resp);

            return mapper.readValue(resp.body(), UsuarioDTO.class);
        } catch (Exception ex) {
            throw new RuntimeException("Error consultando docente activo: " + ex.getMessage(), ex);
        }
    }

    public int countProyectosEnTramiteDocente(String correo) {
        try {
            String url = baseUrlDocente + "/countProyectos/" + enc(correo);

            HttpRequest.Builder rb = HttpRequest.newBuilder(URI.create(url))
                    .timeout(Duration.ofSeconds(8));

            String token = SessionManager.getInstance().getAccessToken();
            if (token != null && !token.isBlank()) {
                rb.header("Authorization", "Bearer " + token);
            }

            HttpRequest req = rb.GET().build();

            HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
            validar2xx(resp);

            return Integer.parseInt(resp.body().trim());
        } catch (Exception ex) {
            throw new RuntimeException("Error consultando conteo de proyectos: " + ex.getMessage(), ex);
        }
    }

    public boolean docenteTieneCupo(String correo) {
        return countProyectosEnTramiteDocente(correo) < 7;
    }

    public int maxVersionAnteproyecto(long id) {
        // TODO: implementar cuando exista endpoint real
        return 1;
    }

    // ---------------- helpers ----------------
    private static void validar2xx(HttpResponse<?> resp) {
        int s = resp.statusCode();
        if (s < 200 || s >= 300) {
            throw new IllegalStateException("HTTP " + s + " - " + resp.body());
        }
    }

    private static String enc(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }
}
