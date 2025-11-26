package co.edu.unicauca.academicprojectservice.application.services;

import co.edu.unicauca.academicprojectservice.port.out.persistence.DbPortDocente;
import co.edu.unicauca.shared.contracts.model.EstadoProyecto;
import org.springframework.stereotype.Service;

@Service
public class DocenteService {

    private final DbPortDocente dbPortDocente;

    public DocenteService(DbPortDocente dbPortDocente) {
        this.dbPortDocente = dbPortDocente;
    }

    //Revisar si se está usando
//    public DocenteDTO obtenerDocentePorCorreo(String correo) {
//        return docenteRepository.findByCorreo(correo)
//                .map(d -> new DocenteDTO(
//                        d.getNombres(),
//                        d.getApellidos(),
//                        d.getCelular(),
//                        d.getCorreo(),
//                        d.getDepartamento().name()
//                ))
//                .orElse(null);
//    }
//
//    public void agregarDocente(DocenteDTO dto) {
//        Docente d = new Docente();
//        d.setNombres(dto.getNombres());
//        d.setApellidos(dto.getApellidos());
//        d.setCelular(dto.getCelular());
//        d.setCorreo(dto.getCorreo());
//        d.setDepartamento(Departamento.valueOf(dto.getDepartamento().toUpperCase().replace(" ", "_")));
//        docenteRepository.save(d);
//    }
    //=============================================================

    public int countProyectosEnTramitePorCorreo(String correo) {
        return dbPortDocente.countByDocenteCorreoAndEstadoNot(correo, EstadoProyecto.FORMATOA_RECHAZADO);
    }
}

