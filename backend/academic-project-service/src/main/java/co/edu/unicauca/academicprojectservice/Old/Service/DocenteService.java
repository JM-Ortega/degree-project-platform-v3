package co.edu.unicauca.academicprojectservice.Old.Service;

import co.edu.unicauca.academicprojectservice.application.dto.DocenteDTO;
import co.edu.unicauca.academicprojectservice.infrastructure.adapters.output.persistence.entity.Docente;
import co.edu.unicauca.academicprojectservice.infrastructure.adapters.output.persistence.repository.DocenteRepository;
import co.edu.unicauca.academicprojectservice.infrastructure.adapters.output.persistence.repository.ProyectoRepository;
import co.edu.unicauca.shared.contracts.model.Departamento;
import co.edu.unicauca.shared.contracts.model.EstadoProyecto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DocenteService {

    @Autowired
    private DocenteRepository docenteRepository;
    @Autowired
    private ProyectoRepository proyectoRepository;

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

//    public int countProyectosEnTramitePorCorreo(String correo) {
//        return proyectoRepository.countByDocenteCorreoAndEstadoNot(correo, EstadoProyecto.FORMATOA_RECHAZADO);
//    }
}

