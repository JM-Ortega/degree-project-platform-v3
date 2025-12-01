package co.edu.unicauca.notificationservice.service;

import co.edu.unicauca.notificationservice.sender.NotificationSender;
import co.edu.unicauca.shared.contracts.events.notification.NotificationEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * Servicio encargado de enviar notificaciones por correo o SMS.
 * Según el tipo de evento, agrega destinatarios adicionales antes de enviar.
 */
@Slf4j
@Service
public class NotificationService {
    /** Manejador para notificaciones por correo. */
    private final NotificationSender emailNotificationSender;

    /** Manejador para notificaciones por correo y SMS. */
    private final NotificationSender smsNotificationSender;

    /** Servicio que provee información relacionada con correos y teléfonos. */
    private final InformationService informationService;

    /**
     * Construye el servicio de notificaciones.
     *
     * @param emailNotificationSender manejador para envío de correo
     * @param smsNotificationSender manejador para envío de SMS
     * @param informationService servicio para consultar datos de contacto
     */
    public NotificationService(
            @Qualifier("emailNotificationSender") NotificationSender emailNotificationSender,
            @Qualifier("smsNotificationSender") NotificationSender smsNotificationSender,
            @Qualifier("informationService") InformationService informationService) {
        this.emailNotificationSender = emailNotificationSender;
        this.smsNotificationSender = smsNotificationSender;
        this.informationService = informationService;
    }

    /**
     * Procesa un evento de notificación.
     * Agrega los destinatarios necesarios y envía por el canal correspondiente.
     *
     * @param event evento con la información de la notificación
     */
    public void notificar(NotificationEvent event) {
        agregarDestinatarios(event);

        if (event.isSMS()) {
            enviarSms(event);
        } else {
            emailNotificationSender.send(event);
        }
    }

    /**
     * Agrega destinatarios según el tipo de evento.
     *
     * @param event evento de notificación
     */
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
        }
    }

    /**
     * Envía una notificación por SMS.
     * Convierte los correos existentes en números de teléfono consultados en el servicio.
     *
     * @param event evento que contiene los datos para el envío
     */
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

    /**
     * Agrega un correo al evento si existe.
     * Si no existe, registra una advertencia.
     *
     * @param email correo encontrado
     * @param contexto descripción del tipo de destinatario faltante
     * @param ref referencia (programa o departamento)
     * @param event evento al que se añadirá el correo
     */
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
