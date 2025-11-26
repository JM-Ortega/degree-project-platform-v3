package co.edu.unicauca.academicprojectservice.port.out.notification;

import co.edu.unicauca.academicprojectservice.domain.model.Proyecto;

public interface NotificationPort {
    void notificarACoordinadores(Proyecto proyectoCreado);
}
