package co.edu.unicauca.frontend.services.auth;

import co.edu.unicauca.frontend.dto.LoginRequestDto;
import co.edu.unicauca.frontend.dto.LoginResponseDto;
import co.edu.unicauca.frontend.dto.RegistroPersonaDto;
import co.edu.unicauca.frontend.dto.SessionInfo;
import co.edu.unicauca.frontend.infra.http.HttpClientException;
import co.edu.unicauca.frontend.infra.operation.LoginValidator;
import co.edu.unicauca.frontend.infra.operation.RegistrationValidator;
import co.edu.unicauca.frontend.infra.session.SessionData;
import co.edu.unicauca.frontend.infra.session.SessionManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Servicio de aplicación del frontend para autenticación y registro.
 *
 * <p>
 * Su función es orquestar:
 * </p>
 * <ul>
 *   <li>Validación previa en cliente (sin llamar al backend).</li>
 *   <li>Llamadas HTTP al microservicio Auth mediante {@link AuthApi}.</li>
 *   <li>Persistencia de la sesión en el cliente mediante {@link SessionManager}.</li>
 * </ul>
 *
 * <p>
 * Además, traduce ciertos mensajes de error del backend (por ejemplo:
 * “Ya existe un usuario con ese correo”) a claves de campos concretos
 * para que la capa JavaFX los pueda mostrar en el label correcto.
 * </p>
 */
public class AuthServiceFront {

    private final AuthApi authApi;

    /**
     * Crea una nueva instancia del servicio de autenticación del frontend.
     *
     * @param authApi implementación concreta usada para llamar al backend.
     */
    public AuthServiceFront(AuthApi authApi) {
        this.authApi = authApi;
    }

    /**
     * Registra una persona en el sistema.
     *
     * <p>Flujo:</p>
     * <ol>
     *   <li>Valida el DTO en cliente con {@link RegistrationValidator}.</li>
     *   <li>Si hay errores de cliente, los devuelve tal cual.</li>
     *   <li>Si no hay errores, llama al backend vía {@link AuthApi#register(RegistroPersonaDto)}.</li>
     *   <li>Si el backend responde 400 con un mensaje conocido, se mapea al campo concreto
     *       (correo, roles, etc.).</li>
     *   <li>Si el backend responde 500/404 u otro, se devuelve un error general.</li>
     * </ol>
     *
     * @param dto datos de la persona a registrar.
     * @return mapa vacío si todo salió bien; mapa campo → mensaje en caso de error.
     * @throws Exception si ocurre un error de E/S no controlado al llamar al backend.
     */
    public Map<String, String> register(RegistroPersonaDto dto) throws Exception {

        // 1. Validación en frontend
        Map<String, String> errors = RegistrationValidator.validate(dto);
        if (!errors.isEmpty()) {
            return errors;
        }

        try {
            // 2. Llamada al backend
            authApi.register(dto);
            return Map.of(); // éxito

        } catch (HttpClientException ex) {
            Map<String, String> mapped = new HashMap<>();
            String body = ex.getResponseBody() != null ? ex.getResponseBody() : "";
            String lower = body.toLowerCase();

            // ---- MANEJO DE STATUS 400 (errores esperados) ----
            if (ex.getStatus() == 400) {

                // EMAIL DUPLICADO (microservicio)
                if (lower.contains("usuario con ese correo")
                        || lower.contains("correo ya existe")
                        || lower.contains("email ya existe")
                        || lower.contains("user exists")) { // <-- Keycloak
                    mapped.put("email", clean(body));
                    return mapped;
                }

                // ROLES INVÁLIDOS
                if (lower.contains("rol") && lower.contains("no")) {
                    mapped.put("roles", clean(body));
                    return mapped;
                }

                // PROBLEMAS CON KEYCLOAK: rol inexistente, cliente incorrecto, etc.
                if (lower.contains("role") && lower.contains("not")) {
                    mapped.put("roles", clean(body));
                    return mapped;
                }

                // Otros errores conocidos → general
                mapped.put("general", clean(body));
                return mapped;
            }

            // ---- MANEJO DE STATUS 500 / 404 / 503 ----
            mapped.put("general", "El servidor devolvió un error (" + ex.getStatus() + ")");
            return mapped;
        }
    }


    /**
     * Variante de inicio de sesión orientada a la capa de presentación (JavaFX).
     *
     * <p>Hace todo en uno:</p>
     * <ol>
     *     <li>Valida en cliente (correo, contraseña, rol).</li>
     *     <li>Si pasa, llama al backend.</li>
     *     <li>Si el backend autentica, guarda la sesión en {@link SessionManager}.</li>
     *     <li>Si el backend rechaza (400/401), devuelve un error con clave {@code "general"}.</li>
     * </ol>
     *
     * @param dto DTO con email, password y rol elegido.
     * @return mapa vacío si login fue correcto; mapa con errores si algo falló.
     */
    public Map<String, String> loginAndReturnErrors(LoginRequestDto dto) {

        // 1. validar en cliente
        Map<String, String> errors = LoginValidator.validate(
                dto.email(),
                dto.password(),
                dto.rol().name()
        );
        if (!errors.isEmpty()) {
            return errors;
        }

        try {
            // 2. llamar al backend
            LoginResponseDto resp = authApi.login(dto);

            // Convertir SessionInfo REVISADA
            SessionInfo info = resp.session();

            // Crear SessionData completo
            SessionData sessionData = new SessionData(
                    resp.accessToken(),
                    resp.refreshToken(),
                    resp.expiresIn(),
                    resp.tokenType(),
                    info,
                    resp.roles()
            );

            // Guardar en el SessionManager
            SessionManager.getInstance().setCurrentSession(sessionData);

            return Map.of(); // ok

        } catch (HttpClientException ex) {
            String body = ex.getResponseBody();
            if (ex.getStatus() == 400 || ex.getStatus() == 401) {
                return Map.of("general", clean(body));
            }
            return Map.of("general", "No fue posible iniciar sesión. Código: " + ex.getStatus());
        } catch (Exception e) {
            return Map.of("general", "No fue posible iniciar sesión. Intente más tarde.");
        }
    }


    /**
     * Variante de inicio de sesión directa (sin mapa de errores).
     *
     * <p>
     * Útil si quieres usar el servicio desde otra capa que prefiera
     * manejar excepciones en lugar de mapas de errores.
     * </p>
     *
     * @param dto credenciales y rol.
     * @throws Exception si el backend rechaza la autenticación o hay un problema de comunicación.
     */
    public void login(LoginRequestDto dto) throws Exception {

        LoginResponseDto resp = authApi.login(dto);

        SessionInfo info = resp.session();

        SessionData sessionData = new SessionData(
                resp.accessToken(),
                resp.refreshToken(),
                resp.expiresIn(),
                resp.tokenType(),
                info,
                resp.roles()
        );

        SessionManager.getInstance().setCurrentSession(sessionData);
    }


    /**
     * Cierra la sesión local (no llama al backend).
     */
    public void logout() {
        SessionManager.getInstance().clear();
    }

    /**
     * Limpia un mensaje de error que viene del backend en formato muy simple
     * ({@code {"error":"..."} }) para mostrarlo directamente en la UI.
     *
     * <p>
     * Este método es deliberadamente sencillo porque el backend ya controla
     * los mensajes. Si en el futuro el backend devuelve estructuras más
     * complejas (JSON con varias claves), convendrá parsearlo con Jackson.
     * </p>
     *
     * @param raw texto devuelto por el backend.
     * @return mensaje limpio, sin llaves ni comillas.
     */
    private String clean(String raw) {
        if (raw == null) {
            return "";
        }
        String s = raw.trim();
        s = s.replace("{", "")
                .replace("}", "")
                .replace("\"", "")
                .replace("error:", "")
                .replace("error", "")
                .trim();
        return s;
    }
}
