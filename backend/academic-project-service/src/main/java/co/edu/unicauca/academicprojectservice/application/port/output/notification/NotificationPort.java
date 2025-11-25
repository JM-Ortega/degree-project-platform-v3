package co.edu.unicauca.academicprojectservice.application.port.output.notification;

import co.edu.unicauca.academicprojectservice.domain.model.Proyecto;

public interface NotificationPort {
    void notificarACoordinadores(Proyecto proyectoCreado);
}
