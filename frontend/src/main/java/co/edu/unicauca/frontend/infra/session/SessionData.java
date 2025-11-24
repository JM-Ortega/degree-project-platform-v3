package co.edu.unicauca.frontend.infra.session;

import co.edu.unicauca.frontend.dto.SessionInfo;

import java.util.List;

/**
 * Representa toda la información de sesión obtenida desde el backend
 * tras un login exitoso con Keycloak.
 * <p>
 * Incluye:
 * - Tokens (access + refresh)
 * - Metadatos de expiración
 * - Información básica del usuario (SessionInfo)
 * - Roles completos del usuario
 * <p>
 * Esta clase es inmutable y se utiliza dentro de SessionManager.
 */
public class SessionData {

    private final String accessToken;
    private final String refreshToken;
    private final Long expiresIn;
    private final String tokenType;

    private final SessionInfo sessionInfo;
    private final List<String> roles;

    public SessionData(
            String accessToken,
            String refreshToken,
            Long expiresIn,
            String tokenType,
            SessionInfo sessionInfo,
            List<String> roles
    ) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
        this.tokenType = tokenType;
        this.sessionInfo = sessionInfo;
        this.roles = roles;
    }

    // ============================
    // Getters directos
    // ============================
    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public String getTokenType() {
        return tokenType;
    }

    public SessionInfo getSessionInfo() {
        return sessionInfo;
    }

    public List<String> getRoles() {
        return roles;
    }

    // ================================================
    // Getters derivados (más cómodos para JavaFX)
    // ================================================

    /**
     * Retorna el email del usuario logueado.
     */
    public String getEmail() {
        return (sessionInfo != null) ? sessionInfo.email() : null;
    }

    /**
     * Retorna el nombre visible del usuario logueado.
     */
    public String getNombreVisible() {
        return (sessionInfo != null) ? sessionInfo.nombres() : null;
    }

    /**
     * Retorna el rol activo usado para iniciar sesión.
     */
    public String getRolActivo() {
        return (sessionInfo != null) ? sessionInfo.rolActivo().name() : null;
    }
}
