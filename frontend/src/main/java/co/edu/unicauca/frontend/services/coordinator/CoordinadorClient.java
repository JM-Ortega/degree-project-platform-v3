package co.edu.unicauca.frontend.services.coordinator;

import co.edu.unicauca.frontend.infra.config.AppConfig;
import co.edu.unicauca.frontend.infra.session.SessionManager;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public class CoordinadorClient {

    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(3))
            .followRedirects(Redirect.NORMAL)
            .build();
    private final String baseUrl;
    private final String infoEndpoint;

    public CoordinadorClient() {
        String b = AppConfig.get("api.base-url");
        if (b == null || b.isBlank()) b = "https://api.narvaezlab.dev/api";
        this.baseUrl = trimRightSlash(b);

        String ep = AppConfig.get("api.endpoint.coord.info");
        if (ep == null || ep.isBlank()) ep = "/coordinator/coordinadores/{correo}/info";
        this.infoEndpoint = ensureLeadingSlash(ep);
    }

    private static String trimRightSlash(String s) {
        return (s.endsWith("/")) ? s.substring(0, s.length() - 1) : s;
    }

    private static String ensureLeadingSlash(String s) {
        return (s.startsWith("/")) ? s : "/" + s;
    }

    public String getCoordinadorInfo(String correo) throws Exception {
        String correoEnc = URLEncoder.encode(correo, StandardCharsets.UTF_8);
        String endpoint = infoEndpoint.replace("{correo}", correoEnc);
        String url = baseUrl + endpoint; // sin dobles //

        HttpRequest.Builder rb = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(5))
                .header("Accept", "application/json");

        String token = SessionManager.getInstance().getAccessToken();
        if (token != null && !token.isBlank()) {
            rb.header("Authorization", "Bearer " + token);
        }

        HttpResponse<String> response = client.send(
                rb.GET().build(),
                HttpResponse.BodyHandlers.ofString()
        );

        int sc = response.statusCode();
        if (sc == 404) {
            throw new NotFoundException("Coordinador no encontrado: " + correo);
        }
        if (sc >= 400 && sc < 500) {
            throw new ClientErrorException("HTTP " + sc + " en " + url + " -> " + response.body());
        }
        if (sc >= 500) {
            throw new ServerErrorException("HTTP " + sc + " en " + url + " -> " + response.body());
        }
        return response.body();
    }

    public static class NotFoundException extends RuntimeException {
        public NotFoundException(String msg) {
            super(msg);
        }
    }

    // ----------------- helpers -----------------

    public static class ClientErrorException extends RuntimeException {
        public ClientErrorException(String msg) {
            super(msg);
        }
    }

    public static class ServerErrorException extends RuntimeException {
        public ServerErrorException(String msg) {
            super(msg);
        }
    }
}
