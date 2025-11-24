package co.edu.unicauca.frontend.infra.http;

import co.edu.unicauca.frontend.dto.LoginRequestDto;
import co.edu.unicauca.frontend.dto.LoginResponseDto;
import co.edu.unicauca.frontend.dto.RegistroPersonaDto;
import co.edu.unicauca.frontend.services.auth.AuthApi;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Implementación concreta de {@link AuthApi} que comunica el frontend con el
 * microservicio de autenticación mediante peticiones HTTP estándar utilizando
 * {@link HttpURLConnection}.
 *
 * <p>
 * Esta clase encapsula la lógica necesaria para serializar objetos DTO a formato
 * JSON mediante {@link ObjectMapper}, enviar las solicitudes HTTP y manejar las
 * respuestas del servidor, incluyendo el lanzamiento de {@link HttpClientException}
 * cuando el backend devuelve un código de error (distinto de 200/201).
 * </p>
 *
 * <p>
 * Está diseñada para ser independiente de frameworks externos, permitiendo su uso
 * en entornos JavaFX o aplicaciones de escritorio sin dependencias adicionales.
 * </p>
 *
 * <h2>Responsabilidades principales:</h2>
 * <ul>
 *   <li>Enviar solicitudes POST al endpoint de autenticación y registro.</li>
 *   <li>Serializar y deserializar los DTOs entre JSON y objetos Java.</li>
 *   <li>Capturar y propagar errores HTTP con detalle del código y cuerpo devuelto.</li>
 * </ul>
 *
 * <p><b>Ejemplo de uso:</b></p>
 * <pre>{@code
 * AuthApi authApi = new HttpAuthApi("http://localhost:8080/api", "/auth/register", "/auth/login");
 * LoginRequestDto dto = new LoginRequestDto("juan@unicauca.edu.co", "Clave123*", Rol.Estudiante);
 * LoginResponseDto response = authApi.login(dto);
 * System.out.println("Token: " + response.token());
 * }</pre>
 *
 * @author Juan
 * @since 1.0
 * @see AuthApi
 * @see HttpClientException
 */
public class HttpAuthApi implements AuthApi {

    /**
     * URL base del gateway o del microservicio.
     * <p>Ejemplo: {@code http://localhost:8080/api}</p>
     */
    private final String baseUrl;

    /**
     * Ruta del endpoint de inicio de sesión.
     * <p>Ejemplo: {@code /auth/login}</p>
     */
    private final String loginPath;

    /**
     * Ruta del endpoint de registro.
     * <p>Ejemplo: {@code /auth/register}</p>
     */
    private final String registerPath;

    /**
     * Mapeador JSON utilizado para serializar y deserializar los cuerpos HTTP.
     */
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Construye un nuevo cliente HTTP para autenticación.
     *
     * @param baseUrl      URL base del gateway o microservicio. No debe ser nula ni vacía.
     * @param registerPath ruta relativa del endpoint de registro (por ejemplo, {@code /auth/register}).
     *                     Si es nula o vacía, se utiliza un valor por defecto.
     * @param loginPath    ruta relativa del endpoint de inicio de sesión (por ejemplo, {@code /auth/login}).
     *                     Si es nula o vacía, se utiliza un valor por defecto.
     * @throws IllegalArgumentException si la URL base es nula o vacía.
     */
    public HttpAuthApi(String baseUrl, String registerPath, String loginPath) {
        if (baseUrl == null || baseUrl.isBlank()) {
            throw new IllegalArgumentException("La URL base no puede ser nula ni vacía.");
        }
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        this.registerPath = normalizePath(registerPath, "/auth/register");
        this.loginPath = normalizePath(loginPath, "/auth/login");
    }

    /**
     * Normaliza una ruta de endpoint asegurando que comience con “/” y que no sea nula.
     *
     * @param path         ruta recibida.
     * @param defaultValue valor por defecto en caso de que la ruta sea nula o vacía.
     * @return la ruta normalizada en formato estándar.
     */
    private String normalizePath(String path, String defaultValue) {
        if (path == null || path.isBlank()) {
            return defaultValue;
        }
        return path.startsWith("/") ? path : "/" + path;
    }

    /**
     * Realiza el inicio de sesión contra el microservicio de autenticación.
     *
     * <p>
     * Serializa el objeto {@link LoginRequestDto} a formato JSON, lo envía mediante
     * una solicitud POST y deserializa la respuesta en un {@link LoginResponseDto}.
     * </p>
     *
     * @param request DTO con las credenciales del usuario y el rol seleccionado.
     * @return objeto {@link LoginResponseDto} con la sesión y token del usuario autenticado.
     * @throws HttpClientException si el backend devuelve un código distinto de 200/201.
     * @throws Exception           si ocurre un error de conexión o deserialización.
     */
    @Override
    public LoginResponseDto login(LoginRequestDto request) throws Exception {
        String endpoint = baseUrl + loginPath;

        HttpURLConnection conn = (HttpURLConnection) new URL(endpoint).openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json");

        try (OutputStream os = conn.getOutputStream()) {
            os.write(mapper.writeValueAsBytes(request));
        }

        int status = conn.getResponseCode();
        if (status != HttpURLConnection.HTTP_OK && status != HttpURLConnection.HTTP_CREATED) {
            String body = readBody(conn.getErrorStream());
            if (body.isEmpty()) {
                body = readBody(conn.getInputStream());
            }
            throw new HttpClientException(status, body);
        }

        try (InputStream is = conn.getInputStream()) {
            return mapper.readValue(is, LoginResponseDto.class);
        }
    }

    /**
     * Envía una solicitud de registro al microservicio de autenticación.
     *
     * <p>
     * Serializa el objeto {@link RegistroPersonaDto} a formato JSON y lo envía mediante una
     * solicitud POST al endpoint configurado. Si el backend responde con un código de error,
     * se lanza una excepción con el cuerpo textual de la respuesta.
     * </p>
     *
     * @param request DTO con la información de la persona a registrar.
     * @throws HttpClientException si el backend devuelve un código de error distinto de 200/201.
     * @throws Exception           si ocurre un error de conexión o serialización.
     */
    @Override
    public void register(RegistroPersonaDto request) throws Exception {
        String endpoint = baseUrl + registerPath;

        HttpURLConnection conn = (HttpURLConnection) new URL(endpoint).openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");


        try (OutputStream os = conn.getOutputStream()) {
            os.write(mapper.writeValueAsBytes(request));
        }

        int status = conn.getResponseCode();
        if (status != HttpURLConnection.HTTP_OK && status != HttpURLConnection.HTTP_CREATED) {
            String body = readBody(conn.getErrorStream());
            if (body.isEmpty()) {
                body = readBody(conn.getInputStream());
            }
            throw new HttpClientException(status, body);
        }
    }

    /**
     * Lee el cuerpo textual de una respuesta HTTP y lo convierte a cadena UTF-8.
     *
     * @param is flujo de entrada devuelto por la conexión (puede ser {@code null}).
     * @return contenido textual del cuerpo, o cadena vacía si el flujo es nulo.
     * @throws Exception si ocurre un error al leer el flujo.
     */
    private String readBody(InputStream is) throws Exception {
        if (is == null) {
            return "";
        }
        return new String(is.readAllBytes(), StandardCharsets.UTF_8);
    }
}
