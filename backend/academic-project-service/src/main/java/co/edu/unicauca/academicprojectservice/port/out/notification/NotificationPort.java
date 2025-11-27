package co.edu.unicauca.academicprojectservice.port.out.notification;

import co.edu.unicauca.academicprojectservice.domain.model.Proyecto;

public interface NotificationPort {
    void notificarProyectoCreado(Proyecto proyectoCreado);
    void notificarAJefes(Proyecto proyectoCreado);
    void notificarFormatoActualizado(Proyecto proyectoCreado);
}
