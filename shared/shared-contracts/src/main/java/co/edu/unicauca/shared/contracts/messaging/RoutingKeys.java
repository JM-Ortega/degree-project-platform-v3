package co.edu.unicauca.shared.contracts.messaging;

/**
 * Claves de enrutamiento estándar usadas en la plataforma.
 * Centralizar aquí evita strings sueltos y facilita el versionado del contrato.
 */
public final class RoutingKeys {

    // ===== Auth =====
    public static final String AUTH_USER_CREATED = "auth.user.created";
    // ===== Proyectos (eventos emitidos por academic-project-service) =====
    public static final String PROJECT_CREATED = "project.created";
    // ===== Formato A =====
    // Evento emitido por academic-project-service cuando cambia el Formato A
    public static final String ACADEMIC_FORMATO_A_CHANGED = "academic.formata.changed";
    public static final String PROJECT_UPDATED = "project.updated";
    // Eventos emitidos por coordinator-service cuando decide sobre el Formato A
    public static final String COORDINATOR_FORMAT_A_APPROVED = "coordinator.formata.approved";
    public static final String COORDINATOR_FORMAT_A_REJECTED = "coordinator.formata.rejected";
    // ===== Anteproyecto =====
    // academic -> department
    public static final String ACADEMIC_ANTEPROYECTO_CREATED = "academic.anteproyecto.created";
    // ===== Departamento =====
    public static final String DEPARTMENT_PROPOSAL_APPROVED = "department.proposal.approved";
    public static final String DEPARTMENT_EVALUADORES_ASIGNADOS = "department.anteproyecto.evaluadores.asignados";
    // ===== Notificaciones (notification-service) =====
    public static final String NOTIFICATION_SEND = "notification.send";

    private RoutingKeys() {
        // Utility class
    }
    public static final String NOTIFICATION_SEND_ANY = "notification.send.*";
}
