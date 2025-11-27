package co.edu.unicauca.academicprojectservice.port.out.messaging;

import co.edu.unicauca.academicprojectservice.domain.model.FormatoA;

public interface FormatoAEventPublisher {
    void publicarFormatoACreado(FormatoA formatoA);
    void publicarFormatoAActualizado(FormatoA formatoA);
}
