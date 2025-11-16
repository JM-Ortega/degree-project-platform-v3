package co.edu.unicauca.shared.contracts.events.auth;

import co.edu.unicauca.shared.contracts.model.Departamento;
import co.edu.unicauca.shared.contracts.model.Programa;
import co.edu.unicauca.shared.contracts.model.Rol;

import java.util.List;
import java.util.UUID;

/**
 * Evento que emite el auth-service cuando se crea un nuevo usuario.
 *
 * <p>Otros microservicios lo consumen para replicar o sincronizar la información
 * básica del usuario en sus propias bases de datos.</p>
 *
 * <p>El campo {@code departamento} puede ser {@code null} si el rol no requiere
 * asociación a un departamento (por ejemplo, Estudiante o Coordinador).</p>
 */
public record UserCreatedEvent(
        UUID id,                     // Identificador único de la entidad
        String nombre,               // Nombre del usuario
        String apellido,             // Apellido del usuario
        String email,                // Correo institucional
        String telefono,             // Telefono del usuario
        Programa programa,           // Programa académico al que pertenece
        Departamento departamento,   // Departamento (puede ser null)
        List<Rol> roles              // Roles asignados al usuario
) {}
