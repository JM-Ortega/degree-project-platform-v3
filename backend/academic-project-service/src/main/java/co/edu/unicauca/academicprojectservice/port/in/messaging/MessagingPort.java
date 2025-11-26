package co.edu.unicauca.academicprojectservice.application.port.output.messaging;

import co.edu.unicauca.academicprojectservice.domain.model.Proyecto;

public interface MessagingPort {
    void publicarMensajeRMQ(Proyecto proyectoCreado);
}
