package co.edu.unicauca.frontend.infra.session;

import co.edu.unicauca.frontend.entities.enums.Rol;

import java.util.List;

/**
 * Administra la sesión actual en el frontend (JavaFX) usando un patrón Singleton.
 * <p>
 * Guarda:
 * - Tokens (access / refresh)
 * - Info básica del usuario (SessionInfo)
 * - Roles completos del usuario
 */
public class SessionManager {

    private static SessionManager instance;

    private SessionData currentSession;

    private SessionManager() {
        // Singleton: constructor privado
    }

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    // ==========================
    // Gestión básica de sesión
    // ==========================

    public SessionData getCurrentSession() {
        return currentSession;
    }

    public void setCurrentSession(SessionData session) {
        this.currentSession = session;
    }

    public void clear() {
        this.currentSession = null;
    }

    public boolean isLoggedIn() {
        return currentSession != null;
    }

    // ==========================
    // Acceso a tokens
    // ==========================

    public String getAccessToken() {
        return (currentSession != null) ? currentSession.getAccessToken() : null;
    }

    public String getRefreshToken() {
        return (currentSession != null) ? currentSession.getRefreshToken() : null;
    }

    public Long getExpiresIn() {
        return (currentSession != null) ? currentSession.getExpiresIn() : null;
    }

    public String getTokenType() {
        return (currentSession != null) ? currentSession.getTokenType() : null;
    }

    // ==========================
    // Acceso a datos de usuario
    // ==========================

    public String getUserEmail() {
        return (currentSession != null) ? currentSession.getEmail() : null;
    }

    public String getUserNombreVisible() {
        return (currentSession != null) ? currentSession.getNombreVisible() : null;
    }

    public String getRolActivo() {
        return (currentSession != null) ? currentSession.getRolActivo() : null;
    }

    public List<String> getRoles() {
        return (currentSession != null) ? currentSession.getRoles() : List.of();
    }

    // ==========================
    // Helpers de autorización
    // ==========================

    /**
     * Verifica si el usuario tiene el rol indicado (por nombre exacto).
     * Ej: hasRole("DOCENTE")
     */
    public boolean hasRole(String rolName) {
        if (!isLoggedIn() || rolName == null) {
            return false;
        }
        return getRoles().contains(rolName);
    }

    /**
     * Verifica si el usuario tiene el rol indicado (versión con enum del frontend).
     * Ej: hasRole(Rol.DOCENTE)
     */
    public boolean hasRole(Rol rol) {
        if (rol == null) {
            return false;
        }
        return hasRole(rol.name());
    }

    /**
     * Verifica si el usuario tiene al menos uno de los roles indicados.
     */
    public boolean hasAnyRole(Rol... roles) {
        if (!isLoggedIn() || roles == null) {
            return false;
        }
        for (Rol rol : roles) {
            if (rol != null && hasRole(rol)) {
                return true;
            }
        }
        return false;
    }

    // ==========================
    // (Opcional) refrescar tokens
    // ==========================

    /**
     * Permite actualizar solo los tokens (por ejemplo, tras un refresh).
     */
    public void updateTokens(String newAccessToken, String newRefreshToken, Long newExpiresIn) {
        if (currentSession == null) {
            return;
        }
        this.currentSession = new SessionData(
                newAccessToken,
                newRefreshToken,
                newExpiresIn,
                currentSession.getTokenType(),
                currentSession.getSessionInfo(),
                currentSession.getRoles()
        );
    }
}
