package co.edu.unicauca.academicprojectservice.port.out.messaging;

import co.edu.unicauca.academicprojectservice.domain.model.Proyecto;

public interface MessagingPort {
    void publicarProyectoCreado(Proyecto proyectoCreado);

    void publicarAnteproyectoSinEvaluadores(Proyecto proyecto);

    void publicarFormatoActualizado(Proyecto proyectoCreado);
}
