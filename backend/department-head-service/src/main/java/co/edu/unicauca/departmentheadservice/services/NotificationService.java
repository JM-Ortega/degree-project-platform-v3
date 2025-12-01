package co.edu.unicauca.departmentheadservice.services;

import co.edu.unicauca.departmentheadservice.entities.Anteproyecto;
import co.edu.unicauca.departmentheadservice.entities.Docente;
import co.edu.unicauca.departmentheadservice.infra.messaging.DepartmentHeadEventsPublisher;
import co.edu.unicauca.departmentheadservice.infra.messaging.NotificationPublisher;
import co.edu.unicauca.shared.contracts.events.notification.NotificationEvent;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationPublisher notificationPublisher;

    public NotificationService(NotificationPublisher notificationPublisher) {
        this.notificationPublisher = notificationPublisher;
    }

    public void notificarEvaluadores(Anteproyecto anteproyecto) {
        List<String> correos = anteproyecto.getEvaluadores().stream()
                .map(Docente::getEmail)
                .toList();

        NotificationEvent notificationEvent = new NotificationEvent(
                "department.proposal.approved",
                correos,
                "👨‍🏫 Ha sido asignado como evaluador",
                "Por favor revisar la plataforma, ha sido asignado como evaluador para un nuevo anteproyecto",
                null,
                OffsetDateTime.now(),
                false
        );

        notificationPublisher.publishEmail(notificationEvent);
    }
}

