package co.edu.unicauca.notificationservice.service;

import co.edu.unicauca.notificationservice.sender.NotificationSender;
import co.edu.unicauca.shared.contracts.events.notification.NotificationEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

    public void notificar (NotificationEvent event){
        switch (event.getTipo()) {

            case "coordinador" -> handleSingleRecipient(
                    informationService.getEmailCoordinador(event.getPrograma().toString()),
                    "coordinador registrado para el programa",
                    event.getPrograma().toString(),
                    event
            );

            case "anteproyecto.created" -> handleSingleRecipient(
                    informationService.getEmailJefeDepartamento(event.getDepartamento().toString()),
                    "jefe de departamento registrado para el departamento",
                    event.getDepartamento().toString(),
                    event
            );
        }

        if (event.isSMS()){
            List<String> telefonos = new ArrayList<>();
            for(String correo : event.getCorreos()){
                String celular = informationService.getTelefono(correo);
                if(celular != null){
                    telefonos.add(celular);
                }
            }
            if(telefonos.isEmpty()){
                log.info("""
            
                            📱 No es posible enviar SMS
                            └── No hay número de telefono registrado
                            """);
            }else{
                event.setTelefonos(telefonos);
                smsNotificationSender.send(event);
            }
        }else {
            emailNotificationSender.send(event);
        }
    }

    private void handleSingleRecipient(
            String email,
            String warningContext,
            String ref,
            NotificationEvent event
    ) {
        if (email == null) {
            log.warn("""
                    ❌📬  No es posible enviar la notificación
                    └── No existe un {} de {}
                    """, warningContext, ref);
            return;
        }
        event.getCorreos().add(email);
    }
}
