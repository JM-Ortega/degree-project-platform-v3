package co.edu.unicauca.notificationservice.service;

import co.edu.unicauca.notificationservice.sender.NotificationSender;
import co.edu.unicauca.shared.contracts.events.notification.NotificationEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class NotificationService {
    private final NotificationSender emailNotificationSender; // solo correo
    private final NotificationSender smsNotificationSender;   // correo + SMS
    private final InformationService informationService;

    public NotificationService(
            @Qualifier("emailNotificationSender") NotificationSender emailNotificationSender,
            @Qualifier("smsNotificationSender") NotificationSender smsNotificationSender,
            @Qualifier("informationService") InformationService informationService) {
        this.emailNotificationSender = emailNotificationSender;
        this.smsNotificationSender = smsNotificationSender;
        this.informationService = informationService;
    }

    public void notificar(NotificationEvent event) {
        agregarDestinatarios(event);

        if (event.isSMS()) {
            enviarSms(event);
        } else {
            emailNotificationSender.send(event);
        }
    }

    private void agregarDestinatarios(NotificationEvent event) {
        switch (event.getTipo()) {
            case "coordinador" -> addIfExists(
                    informationService.getEmailCoordinador(event.getPrograma().toString()),
                    "coordinador registrado para el programa",
                    event.getPrograma().toString(),
                    event
            );
            case "anteproyecto.created" -> addIfExists(
                    informationService.getEmailJefeDepartamento(event.getDepartamento().toString()),
                    "jefe de departamento registrado para el departamento",
                    event.getDepartamento().toString(),
                    event
            );
            default -> log.warn("Tipo de notificación desconocido: {}", event.getTipo());
        }
    }

    private void enviarSms(NotificationEvent event) {
        List<String> telefonos = event.getCorreos().stream()
                .map(informationService::getTelefono)
                .filter(Objects::nonNull)
                .toList();

        if (telefonos.isEmpty()) {
            log.info("""
                        
                        📱 No es posible enviar SMS
                        └── No hay número de telefono registrado
                        """);
            return;
        }

        event.setTelefonos(telefonos);
        smsNotificationSender.send(event);
    }

    private void addIfExists(String email, String contexto, String ref, NotificationEvent event) {
        if (email == null) {
            log.warn("""
                    
                    ❌📬  No es posible enviar la notificación
                    └── No existe un {} de {}
                    """, contexto, ref);
            return;
        }
        event.getCorreos().add(email);
    }
}
