package co.edu.unicauca.frontend.infra.http;

import co.edu.unicauca.frontend.dto.AnteproyectoDto;
import co.edu.unicauca.frontend.infra.session.SessionManager;
import co.edu.unicauca.frontend.services.departmenthead.DepartmentHeadApi;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación HTTP del servicio de Jefe de Departamento
 * Gestiona las comunicaciones con el backend para operaciones relacionadas con anteproyectos
 */
public class HttpDepartmentHeadApi implements DepartmentHeadApi {

    private final String baseUrl;
    private final String sinEvaluadoresPath;
    private final String buscarPath;
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Constructor para inicializar el cliente HTTP
     *
     * @param baseUrl            URL base del servicio
     * @param sinEvaluadoresPath ruta para obtener anteproyectos sin evaluadores
     * @param buscarPath         ruta para búsqueda de anteproyectos
     */
    public HttpDepartmentHeadApi(String baseUrl, String sinEvaluadoresPath, String buscarPath) {
        if (baseUrl == null || baseUrl.isBlank()) {
            throw new IllegalArgumentException("La URL base no puede ser nula ni vacía.");
        }
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        this.sinEvaluadoresPath = normalizePath(sinEvaluadoresPath, "/sin-evaluadores");
        this.buscarPath = normalizePath(buscarPath, "/buscar");
    }

    /**
     * Normaliza una ruta de endpoint asegurando el formato correcto
     */
    private String normalizePath(String path, String defaultValue) {
        if (path == null || path.isBlank()) {
            return defaultValue;
        }
        return path.startsWith("/") ? path : "/" + path;
    }

    @Override
    public List<AnteproyectoDto> obtenerAnteproyectosSinEvaluadores() throws Exception {
        String endpoint = baseUrl + sinEvaluadoresPath;

        HttpURLConnection conn = (HttpURLConnection) new URL(endpoint).openConnection();
        conn.setRequestMethod("GET");

        String token = SessionManager.getInstance().getAccessToken();
        if (token != null && !token.isBlank()) {
            conn.setRequestProperty("Authorization", "Bearer " + token);
        }


        int status = conn.getResponseCode();
        if (status != HttpURLConnection.HTTP_OK) {
            String body = readBody(conn.getErrorStream());
            throw new HttpClientException(status, body);
        }

        try (InputStream is = conn.getInputStream()) {
            return mapper.readValue(is,
                    mapper.getTypeFactory().constructCollectionType(List.class, AnteproyectoDto.class));
        }
    }

    @Override
    public List<AnteproyectoDto> buscarPorNombreOIdSinEvaluadores(String nombre, String id) throws Exception {
        StringBuilder urlBuilder = new StringBuilder(baseUrl + buscarPath);
        List<String> parametros = new ArrayList<>();

        if (nombre != null && !nombre.trim().isEmpty()) {
            parametros.add("nombre=" + URLEncoder.encode(nombre.trim(), StandardCharsets.UTF_8));
        }
        if (id != null && !id.trim().isEmpty()) {
            parametros.add("id=" + URLEncoder.encode(id.trim(), StandardCharsets.UTF_8));
        }

        if (!parametros.isEmpty()) {
            urlBuilder.append("?").append(String.join("&", parametros));
        }

        String urlFinal = urlBuilder.toString();
        HttpURLConnection conn = (HttpURLConnection) new URL(urlFinal).openConnection();
        conn.setRequestMethod("GET");

        String token = SessionManager.getInstance().getAccessToken();
        if (token != null && !token.isBlank()) {
            conn.setRequestProperty("Authorization", "Bearer " + token);
        }


        int status = conn.getResponseCode();
        if (status != HttpURLConnection.HTTP_OK) {
            String body = readBody(conn.getErrorStream());
            throw new HttpClientException(status, body);
        }

        try (InputStream is = conn.getInputStream()) {
            return mapper.readValue(is,
                    mapper.getTypeFactory().constructCollectionType(List.class, AnteproyectoDto.class));
        }
    }

    /**
     * Lee el cuerpo de la respuesta HTTP
     */
    private String readBody(InputStream is) throws Exception {
        if (is == null) {
            return "";
        }
        return new String(is.readAllBytes(), StandardCharsets.UTF_8);
    }
}