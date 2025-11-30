package co.edu.unicauca.shared.contracts.events.departmenthead;

import java.util.List;
import java.util.UUID;

public record AnteproyectoConEvaluadoresEvent(
    UUID proyectoId,
    UUID anteproyectoId,
    String titulo,
    String departamento,
    List<String> evaluadores // correos de docentes
) { }

