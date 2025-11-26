package co.edu.unicauca.academicprojectservice.adapter.out.persistence;

import co.edu.unicauca.academicprojectservice.domain.model.EstudianteId;
import co.edu.unicauca.academicprojectservice.port.out.persistence.DbPortEstudiante;

import java.util.Optional;

public class DbAdapterEstudiante implements DbPortEstudiante {

    @Override
    public Optional<EstudianteId> findIdByCorreo(String correo) {
        return Optional.empty();//TODO
    }
}
