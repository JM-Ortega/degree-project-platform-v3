package co.edu.unicauca.academicprojectservice.port.out.messaging;

import co.edu.unicauca.academicprojectservice.domain.model.Proyecto;

public interface MessagingPort {
    void publicarMensajeRMQ(Proyecto proyectoCreado);
}
