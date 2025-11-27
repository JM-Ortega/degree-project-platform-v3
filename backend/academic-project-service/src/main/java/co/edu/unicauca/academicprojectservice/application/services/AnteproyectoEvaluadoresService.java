package co.edu.unicauca.academicprojectservice.application.services;

import co.edu.unicauca.academicprojectservice.port.in.messaging.AnteproyectoEvaluadoresUseCase;
import co.edu.unicauca.shared.contracts.events.departmenthead.AnteproyectoConEvaluadoresEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnteproyectoEvaluadoresService implements AnteproyectoEvaluadoresUseCase {

    private final ProyectoService proyectoService;

    @Override
    public void procesarAsignacionEvaluadores(AnteproyectoConEvaluadoresEvent event) {
        proyectoService.asignarEvaluadoresAnteproyectoDesdeDeptHead(event);
    }
}
