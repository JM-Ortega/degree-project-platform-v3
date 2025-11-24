package co.edu.unicauca.frontend.services.coordinator;

import co.edu.unicauca.frontend.entities.FormatoAResumen;
import co.edu.unicauca.frontend.entities.FormatoAResumenDTO;
import co.edu.unicauca.frontend.infra.config.AppConfig;
import co.edu.unicauca.frontend.infra.session.SessionManager;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

public class FormatoService {

    private final HttpClient httpClient;
    private final String baseUrl;   // http://localhost:8080/api
    private final String epListar;  // /coordinator/formatoA/listar/{programa}

    private static final String BOUNDARY = "---JavaBoundary";
    private static final String LINE_FEED = "\r\n";

    private final ObjectMapper mapper;
    private final String epGet;     // /coordinator/formatoA/{id}
    private final String epDownload;// /coordinator/formatoA/{id}/descargar
    private final String epUpdate;  // /coordinator/formatoA/actualizar/{id}
    public FormatoService() {
        this.mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(3))
                .followRedirects(Redirect.NORMAL)
                .build();

        String b = AppConfig.get("api.base-url");
        if (b == null || b.isBlank()) b = "http://localhost:8080/api";
        this.baseUrl = trimRightSlash(b);

        this.epListar = ensureLeadingSlash(orDefault("api.endpoint.formatoa.listar", "/coordinator/formatoA/listar/{programa}"));
        this.epGet = ensureLeadingSlash(orDefault("api.endpoint.formatoa.get", "/coordinator/formatoA/{id}"));
        this.epDownload = ensureLeadingSlash(orDefault("api.endpoint.formatoa.descargar", "/coordinator/formatoA/{id}/descargar"));
        this.epUpdate = ensureLeadingSlash(orDefault("api.endpoint.formatoa.actualizar", "/coordinator/formatoA/actualizar/{id}"));
    }

    private static String trimRightSlash(String s) {
        return (s.endsWith("/")) ? s.substring(0, s.length() - 1) : s;
    }

    private static String ensureLeadingSlash(String s) {
        return (s.startsWith("/")) ? s : "/" + s;
    }

    private String orDefault(String key, String def) {
        String v = AppConfig.get(key);
        return (v == null || v.isBlank()) ? def : v;
    }

    /**
     * Lista de Formato A (resumen) por programa vía Gateway
     */
    public List<FormatoAResumen> obtenerFormatosAResumen() {
        String programa = "INGENIERIA_DE_SISTEMAS";
        String programaEnc = URLEncoder.encode(programa.toUpperCase().trim(), StandardCharsets.UTF_8);
        String url = baseUrl + epListar.replace("{programa}", programaEnc);

        try {
            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(6))
                    .header("Accept", "application/json");

            String token = SessionManager.getInstance().getAccessToken();
            if (token != null && !token.isBlank()) {
                builder.header("Authorization", "Bearer " + token);
            }

            HttpRequest request = builder.GET().build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            handleErrors(url, response);

            List<FormatoAResumenDTO> dtoList = mapper.readValue(
                    response.body(),
                    new TypeReference<List<FormatoAResumenDTO>>() {
                    }
            );

            return dtoList.stream()
                    .map(dto -> new FormatoAResumen(
                            dto.getId(),
                            dto.getNombreProyecto(),
                            dto.getNombreDirector(),
                            dto.getTipoProyecto() != null ? dto.getTipoProyecto().toString() : "",
                            dto.getFechaSubida(),
                            dto.getEstadoFormatoA() != null ? dto.getEstadoFormatoA().toString() : "",
                            dto.getNroVersion(),
                            dto.getNombreFormatoA()
                    ))
                    .collect(Collectors.toList());

        } catch (RuntimeException re) {
            throw re;
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }


    /**
     * Descargar PDF vía Gateway (devuelve bytes)
     */
    public byte[] descargarFormatoA(Long id) throws IOException, InterruptedException {
        String url = baseUrl + epDownload.replace("{id}", String.valueOf(id));

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(10))
                .header("Accept", "application/pdf");

        String token = SessionManager.getInstance().getAccessToken();
        if (token != null && !token.isBlank()) {
            builder.header("Authorization", "Bearer " + token);
        }

        HttpRequest request = builder.GET().build();

        HttpResponse<byte[]> resp = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
        handleErrors(url, resp);
        return resp.body();
    }


    /**
     * PUT multipart/form-data vía Gateway
     */
    public HttpResponse<String> actualizarFormato(
            Long formatoId, String nuevoEstado, File archivo, String nombreArchivo, String horaActual
    ) throws IOException, InterruptedException {

        HttpRequest.BodyPublisher body = buildMultipartBody(archivo, nuevoEstado, nombreArchivo, horaActual);
        String url = baseUrl + epUpdate.replace("{id}", String.valueOf(formatoId));

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(20))
                .header("Content-Type", "multipart/form-data; boundary=" + BOUNDARY)
                .header("Accept", "application/json");

        String token = SessionManager.getInstance().getAccessToken();
        if (token != null && !token.isBlank()) {
            builder.header("Authorization", "Bearer " + token);
        }

        HttpRequest request = builder.PUT(body).build();

        HttpResponse<String> resp = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        handleErrors(url, resp);
        return resp;
    }


    // ---------------------------------------------------------------------
    // API
    // ---------------------------------------------------------------------

    private void handleErrors(String url, HttpResponse<?> response) {
        int sc = response.statusCode();
        if (sc == 404) throw new NotFoundException("Recurso no encontrado en " + url);
        if (sc >= 400 && sc < 500) throw new ClientErrorException("HTTP " + sc + " en " + url);
        if (sc >= 500) throw new ServerErrorException("HTTP " + sc + " en " + url);
    }

    private HttpRequest.BodyPublisher buildMultipartBody(
            File archivo, String nuevoEstado, String nombreArchivo, String horaActual
    ) throws IOException {

        StringBuilder sb = new StringBuilder();

        // Campo estado
        sb.append("--").append(BOUNDARY).append(LINE_FEED)
                .append("Content-Disposition: form-data; name=\"nuevoEstado\"").append(LINE_FEED)
                .append(LINE_FEED).append(nuevoEstado).append(LINE_FEED);

        // Campo nombre del archivo
        sb.append("--").append(BOUNDARY).append(LINE_FEED)
                .append("Content-Disposition: form-data; name=\"nombreArchivo\"").append(LINE_FEED)
                .append(LINE_FEED).append(nombreArchivo).append(LINE_FEED);

        // Campo hora actual
        sb.append("--").append(BOUNDARY).append(LINE_FEED)
                .append("Content-Disposition: form-data; name=\"horaActual\"").append(LINE_FEED)
                .append(LINE_FEED).append(horaActual).append(LINE_FEED);

        // Campo archivo PDF
        sb.append("--").append(BOUNDARY).append(LINE_FEED)
                .append("Content-Disposition: form-data; name=\"archivo\"; filename=\"")
                .append(archivo.getName()).append("\"").append(LINE_FEED)
                .append("Content-Type: application/pdf").append(LINE_FEED)
                .append(LINE_FEED);

        byte[] inicio = sb.toString().getBytes(StandardCharsets.UTF_8);
        byte[] archivoBytes = Files.readAllBytes(archivo.toPath());
        byte[] fin = (LINE_FEED + "--" + BOUNDARY + "--" + LINE_FEED).getBytes(StandardCharsets.UTF_8);

        return HttpRequest.BodyPublishers.ofByteArrays(List.of(inicio, archivoBytes, fin));
    }

    public static class NotFoundException extends RuntimeException {
        public NotFoundException(String m) {
            super(m);
        }
    }

    // ---------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------

    public static class ClientErrorException extends RuntimeException {
        public ClientErrorException(String m) {
            super(m);
        }
    }

    public static class ServerErrorException extends RuntimeException {
        public ServerErrorException(String m) {
            super(m);
        }
    }
}
