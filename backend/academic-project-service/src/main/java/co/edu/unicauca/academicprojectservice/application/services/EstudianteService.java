package co.edu.unicauca.academicprojectservice.application.services;

import co.edu.unicauca.academicprojectservice.adapter.out.persistence.repository.EstudianteRepository;
import co.edu.unicauca.academicprojectservice.adapter.out.persistence.repository.FormatoARepository;
import co.edu.unicauca.shared.contracts.model.EstadoFormatoA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EstudianteService {
    @Autowired
    private EstudianteRepository estudianteRepository;

    @Autowired
    private FormatoARepository formatoARepository;

    public boolean existeEstudiantePorCorreo(String correo) {
        if (correo == null || correo.trim().isEmpty()) {
            return false;
        }
        return estudianteRepository.findByCorreoIgnoreCase(correo).isPresent();
    }

    public boolean estudianteTieneProyectoEnTramitePorCorreo(String correo) {
        if (correo == null || correo.trim().isEmpty()) {
            return false;
        }
        return estudianteRepository.tieneProyectoEnTramite(correo);
    }

    public boolean estudianteTieneFormatoAAprobado(String correo) {
        return formatoARepository.existeFormatoAAprobadoPorCorreo(correo, EstadoFormatoA.APROBADO);
    }
}
