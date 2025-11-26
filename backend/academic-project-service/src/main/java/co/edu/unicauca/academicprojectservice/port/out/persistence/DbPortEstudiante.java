package co.edu.unicauca.academicprojectservice.application.port.output;

import co.edu.unicauca.academicprojectservice.domain.model.EstudianteId;

import java.util.Optional;

public interface DbPortEstudiante {

    Optional<EstudianteId> findIdByCorreo(String correo);
}
