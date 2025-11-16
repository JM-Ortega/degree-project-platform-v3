package co.edu.unicauca.academicprojectservice.Service;

import co.edu.unicauca.academicprojectservice.Entity.EstadoFormatoA;
import co.edu.unicauca.academicprojectservice.Entity.Estudiante;
import co.edu.unicauca.shared.contracts.model.Programa;
import co.edu.unicauca.academicprojectservice.Entity.Proyecto;
import co.edu.unicauca.academicprojectservice.Repository.EstudianteRepository;
import co.edu.unicauca.academicprojectservice.Repository.FormatoARepository;
import co.edu.unicauca.academicprojectservice.infra.dto.EstudianteDTO;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EstudianteService {
    @Autowired
    private EstudianteRepository estudianteRepository;
    @Autowired
    private FormatoARepository formatoARepository;

    public EstudianteDTO obtenerEstudiantePorCorreo(String correo) {
        Estudiante e = estudianteRepository.findByCorreoIgnoreCase(correo)
                .orElseThrow(() -> new IllegalArgumentException("El correo no pertenece a un estudiante"));
        return new EstudianteDTO(
                e.getNombres(),
                e.getApellidos(),
                e.getCelular(),
                e.getCorreo(),
                e.getPrograma() != null ? e.getPrograma().toString() : ""
        );
    }

    public void agregarEstudiante(EstudianteDTO dto) {
        Estudiante e = new Estudiante();
        e.setNombres(dto.getNombres());
        e.setApellidos(dto.getApellidos());
        e.setCelular(dto.getCelular());
        e.setCorreo(dto.getCorreo());

        String progStr = dto.getPrograma().toUpperCase().replace(" ", "_");
        try {
            Programa prog = Programa.valueOf(progStr);
            e.setPrograma(prog);
        } catch (IllegalArgumentException ex) {
            System.out.println("Programa inválido: " + dto.getPrograma());
            e.setPrograma(null);
        }

        estudianteRepository.save(e);
    }

    /**
     * Verifica si existe un estudiante registrado con el correo dado.
     *
     * @param correo correo del estudiante
     * @return true si el estudiante existe, false si no
     */
    public boolean existeEstudiantePorCorreo(String correo) {
        if (correo == null || correo.trim().isEmpty()) {
            return false;
        }
        return estudianteRepository.findByCorreoIgnoreCase(correo).isPresent();
    }

    /**
     * Verifica si el estudiante con el correo dado tiene un proyecto en trámite.
     *
     * @param correo correo del estudiante
     * @return true si tiene proyecto en trámite, false si no
     */
    public boolean estudianteTieneProyectoEnTramitePorCorreo(String correo) {
        if (correo == null || correo.trim().isEmpty()) {
            return false;
        }
        return estudianteRepository.tieneProyectoEnTramite(correo);
    }

    public boolean estudianteTieneFormatoAAprobado(String correo) {
        return formatoARepository.existeFormatoAAprobadoPorCorreo(correo, EstadoFormatoA.APROBADO);
    }

    public boolean estudianteTieneAnteproyectoAsociado(String correo) {
        Estudiante estudiante = estudianteRepository.findByCorreoIgnoreCase(correo)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró el estudiante con correo: " + correo));

        List<Proyecto> proyectos = estudiante.getTrabajos();
        if (proyectos == null || proyectos.isEmpty()) {
            return false;
        }

        return proyectos.stream()
                .anyMatch(p -> p.getAnteproyecto() != null);
    }
}
