package co.edu.unicauca.academicprojectservice.adapter.out.persistence;

import co.edu.unicauca.academicprojectservice.adapter.out.persistence.repository.EstudianteRepository;
import co.edu.unicauca.academicprojectservice.adapter.out.persistence.repository.FormatoARepository;
import co.edu.unicauca.academicprojectservice.domain.model.EstudianteId;
import co.edu.unicauca.academicprojectservice.port.out.persistence.DbPortEstudiante;
import co.edu.unicauca.shared.contracts.model.EstadoFormatoA;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DbAdapterEstudiante implements DbPortEstudiante {

    private final EstudianteRepository estudianteRepository;
    private  final FormatoARepository formatoARepository;

    public DbAdapterEstudiante(EstudianteRepository estudianteRepository, FormatoARepository formatoARepository) {
        this.estudianteRepository = estudianteRepository;
        this.formatoARepository = formatoARepository;
    }

    @Override
    public Optional<EstudianteId> findIdByCorreo(String correo) {
        return estudianteRepository.findByCorreoIgnoreCase(correo)
                .map(estudiante -> new EstudianteId(estudiante.getId()));
    }

    @Override
    public boolean proyectoActivo(String correo){
        return estudianteRepository.tieneProyectoActivo(correo);
    }

    @Override
    public boolean formatoAAprobadoPorCorreo(String correo, EstadoFormatoA estado){
        return formatoARepository.existeFormatoAAprobadoPorCorreo(correo, estado);
    }
}
