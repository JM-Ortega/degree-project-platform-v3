package co.edu.unicauca.shared.contracts.events.academic;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Evento emitido por academic-project-service cuando se crea un anteproyecto,
 * aún sin evaluadores asignados.
 */
public record AnteproyectoSinEvaluadoresEvent(
        UUID proyectoId,         // ID del proyecto al que pertenece
        UUID anteproyectoId,     // ID del anteproyecto
        String titulo,           // Título del anteproyecto
        String descripcion,      // Descripción del anteproyecto
        LocalDate fechaCreacion, // Fecha de creación
        String estudianteCorreo, // Correo institucional del estudiante
        String directorCorreo,   // Correo del docente director
        String departamento       // Departamento responsable (opcional)
) {}
