package co.edu.unicauca.academicprojectservice.application.services;

import co.edu.unicauca.academicprojectservice.domain.model.Proyecto;
import co.edu.unicauca.academicprojectservice.port.out.persistence.DbPortEstudiante;
import co.edu.unicauca.academicprojectservice.port.out.persistence.DbPortProyecto;
import co.edu.unicauca.shared.contracts.model.EstadoFormatoA;
import org.springframework.stereotype.Service;

@Service
public class EstudianteService {
    private final DbPortEstudiante dbPortEstudiante;
    private final DbPortProyecto dbPortProyecto;

    public EstudianteService(DbPortEstudiante dbPortEstudiante, DbPortProyecto dbPortProyecto) {
        this.dbPortEstudiante = dbPortEstudiante;
        this.dbPortProyecto = dbPortProyecto;
    }

    public boolean existeEstudiantePorCorreo(String correo) {
        if (correo == null || correo.trim().isEmpty()) {
            return false;
        }
        return dbPortEstudiante.findIdByCorreo(correo).isPresent();
    }

    public boolean estudianteTieneProyectoEnTramitePorCorreo(String correo) {
        if (correo == null || correo.trim().isEmpty()) {
            return false;
        }
        return dbPortEstudiante.proyectoEnTramite(correo);
    }

    public boolean estudianteTieneFormatoAAprobado(String correo) {
        return dbPortEstudiante.formatoAAprobadoPorCorreo(correo, EstadoFormatoA.APROBADO);
    }

    public boolean estudianteTieneAnteproyectoAsociado(String correo) {
        Proyecto proyecto = dbPortProyecto.buscarPorCorreo(correo);
        if(proyecto.getAnteproyecto() != null){
            return true;
        }else{
            return false;
        }
    }
}
