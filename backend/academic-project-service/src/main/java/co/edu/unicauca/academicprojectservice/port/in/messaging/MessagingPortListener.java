package co.edu.unicauca.academicprojectservice.port.in.messaging;

import co.edu.unicauca.academicprojectservice.domain.model.Proyecto;

public interface MessagingPortListener {
    void publicarMensajeRMQ(Proyecto proyectoCreado);
}
