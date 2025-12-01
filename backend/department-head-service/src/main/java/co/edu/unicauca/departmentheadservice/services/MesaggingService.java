package co.edu.unicauca.departmentheadservice.services;

import co.edu.unicauca.departmentheadservice.entities.Anteproyecto;
import co.edu.unicauca.departmentheadservice.entities.Docente;
import co.edu.unicauca.departmentheadservice.infra.messaging.DepartmentHeadEventsPublisher;
import co.edu.unicauca.shared.contracts.events.departmenthead.AnteproyectoConEvaluadoresEvent;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MesaggingService {
    private final DepartmentHeadEventsPublisher departmentHeadEventsPublisher;

    public MesaggingService(DepartmentHeadEventsPublisher departmentHeadEventsPublisher) {
        this.departmentHeadEventsPublisher = departmentHeadEventsPublisher;
    }

    public void publicarMensaje (Anteproyecto anteproyecto) {
        List<String> correos = anteproyecto.getEvaluadores().stream()
                .map(Docente::getEmail)
                .toList();

        AnteproyectoConEvaluadoresEvent event = new AnteproyectoConEvaluadoresEvent(anteproyecto.getProyectoId(),
                anteproyecto.getAnteproyectoId(), anteproyecto.getTitulo(), anteproyecto.getDepartamento(), correos);

        departmentHeadEventsPublisher.publishAnteproyectoConEvaluadoresEvent(event);
    }
}
