package co.edu.unicauca.shared.contracts.messaging;

/**
 * Claves de enrutamiento estándar usadas en la plataforma.
 * Mantener aquí evita strings sueltos en cada micro.
 */
public final class RoutingKeys {

    private RoutingKeys() {}

    // ===== dominio (eventos "reales" del sistema) =====
    public static final String AUTH_USER_CREATED = "auth.user.created";
    public static final String PROJECT_CREATED = "project.created";
    public static final String PROJECT_UPDATED = "project.updated";
    public static final String COORDINATOR_FORMAT_A_APPROVED = "coordinator.formata.approved";
    public static final String DEPARTMENT_PROPOSAL_APPROVED = "department.proposal.approved";

    // ===== evento del anteproyecto =====
    public static final String ACADEMIC_ANTEPROYECTO_CREATED = "academic.anteproyecto.created";
    public static final String DEPARTMENT_EVALUADORES_ASIGNADOS = "department.anteproyecto.evaluadores.asignados";

    // ===== notificaciones (solo para notification-service) =====
    public static final String NOTIFICATION_SEND = "notification.send";
    public static final String NOTIFICATION_SEND_ANY = "notification.send.*";
}
