package co.edu.unicauca.academicprojectservice.infraestructura.adapter.output.notification;

import co.edu.unicauca.academicprojectservice.application.port.output.DbPort;
import co.edu.unicauca.academicprojectservice.application.port.output.notification.NotificationPort;
import co.edu.unicauca.academicprojectservice.domain.model.Proyecto;
import co.edu.unicauca.academicprojectservice.infraestructura.adapter.output.persistence.entity.Docente;
import co.edu.unicauca.academicprojectservice.infraestructura.adapter.output.persistence.entity.Estudiante;
import co.edu.unicauca.shared.contracts.events.notification.NotificationEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class NotificationAdapter implements NotificationPort {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${messaging.exchange.main}")
    private String mainExchange;

    private final DbPort dbPort;

    public NotificationAdapter(DbPort dbPort) {
        this.dbPort = dbPort;
    }

    public void notificarACoordinadores(Proyecto proyectoCreado){
        try {
            Estudiante estudiante1 = dbPort.obtenerEstudiantePorId(proyectoCreado.getEstudiantesId().getFirst().value());

            Docente director = dbPort.obtenerDocentePorId(proyectoCreado.getDirectorId().value());

            String est1 = estudiante1.getNombres() + " " + estudiante1.getApellidos();
            String est2 = "";

            List<String> correos = new ArrayList<>();
            correos.add(estudiante1.getCorreo());

            if(proyectoCreado.getEstudiantesId().size()==2){
                Estudiante estudiante2 = dbPort.obtenerEstudiantePorId(proyectoCreado.getEstudiantesId().getLast().value());
                est2 = estudiante2.getNombres() + " " + estudiante2.getApellidos();
                correos.add(estudiante2.getCorreo());
            }

            String subject = "Nuevo Proyecto Creado";
            String message = String.format(
                    """
                            Se ha creado el proyecto:
                            '%s'
                            Estudiante(s):
                            %s
                            %s
                            Bajo la dirección de:
                            %s %s
                    """,
                    proyectoCreado.getTitulo(),
                    est1,
                    est2,
                    director.getNombres(), director.getApellidos()
            );

            NotificationEvent notificationEvent = new NotificationEvent(
                    "project.created",
                    correos,
                    subject,
                    message,
                    estudiante1.getPrograma(),
                    OffsetDateTime.now()
            );

            rabbitTemplate.convertAndSend(mainExchange, "notification.send.project.created", notificationEvent);

            log.info("📨 Notificación enviada: {}", notificationEvent.getAsunto());

        } catch (Exception e) {
            log.error("Error al enviar notificación: {}", e.getMessage(), e);
        }
    }
}
