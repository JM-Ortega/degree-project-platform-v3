package co.edu.unicauca.academicprojectservice.port.in.messaging;

import co.edu.unicauca.shared.contracts.events.departmenthead.AnteproyectoConEvaluadoresEvent;

public interface AnteproyectoEvaluadoresUseCase {
    void procesarAsignacionEvaluadores(AnteproyectoConEvaluadoresEvent event);
}
