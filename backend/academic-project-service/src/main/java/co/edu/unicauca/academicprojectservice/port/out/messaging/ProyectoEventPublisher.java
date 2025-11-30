package co.edu.unicauca.academicprojectservice.port.out.messaging;

import co.edu.unicauca.academicprojectservice.domain.model.Proyecto;

public interface ProyectoEventPublisher {
    void publicarProyectoCreado(Proyecto proyecto);
    void publicarProyectoActualizado(Proyecto proyecto);
}