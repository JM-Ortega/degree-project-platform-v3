package co.edu.unicauca.departmentheadservice.services;

import co.edu.unicauca.departmentheadservice.entities.Anteproyecto;
import co.edu.unicauca.departmentheadservice.entities.Docente;
import co.edu.unicauca.shared.contracts.events.notification.NotificationEvent;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class NotificationService {

    public void notificarEvaluadores(Anteproyecto anteproyecto){
        List<String> correos = new ArrayList<String>();
        for(Docente d : anteproyecto.getEvaluadores()){
            correos.add(d.getEmail());
        }
        NotificationEvent notificationEvent = new NotificationEvent
                ("department.proposal.approved",
                        correos,
                        "👨‍🏫 Ha sido asignado como evaluador",
                        "Por favor revisar la plataforma, ha sido asignado como evaluador paar un nuevo anteproyecto",
                        null,
                        OffsetDateTime.now(),
                        false);
    }
}
