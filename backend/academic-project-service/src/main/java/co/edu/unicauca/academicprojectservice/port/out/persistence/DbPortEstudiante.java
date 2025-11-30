package co.edu.unicauca.academicprojectservice.port.out.persistence;

import co.edu.unicauca.academicprojectservice.domain.model.EstudianteId;
import co.edu.unicauca.shared.contracts.model.EstadoFormatoA;

import java.util.Optional;

public interface DbPortEstudiante {

    Optional<EstudianteId> findIdByCorreo(String correo);

    boolean proyectoActivo(String correo);

    boolean formatoAAprobadoPorCorreo(String correo, EstadoFormatoA estado);
}
