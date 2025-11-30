package co.edu.unicauca.academicprojectservice.port.out.messaging;

import co.edu.unicauca.shared.contracts.events.academic.AnteproyectoSinEvaluadoresEvent;

public interface AnteproyectoEventPublisher {
    void publicarAnteproyectoSinEvaluadores(AnteproyectoSinEvaluadoresEvent event);
}