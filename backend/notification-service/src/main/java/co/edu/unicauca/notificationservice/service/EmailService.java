package co.edu.unicauca.notificationservice.service;

import co.edu.unicauca.shared.contracts.events.notification.NotificationEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class EmailService {
    private InformationService informationService;

    public void sendEmail(NotificationEvent event) {
        if (event.getTipo().equals("coordinador")){
            List<String> emails = event.getCorreos();
            String emailCoordinador = informationService.getEmailCoordinador(event.getPrograma().toString());
            emails.add(emailCoordinador);
            event.setCorreos(emails);
        }

        for (String email : event.getCorreos()) {
            log.info("📩 Enviando correo a: {} | Asunto: {} | Mensaje: {}",
                    email, event.getAsunto(), event.getMensaje());
        }
    }
}