package co.edu.unicauca.academicprojectservice.Service;

import co.edu.unicauca.shared.contracts.model.Departamento;
import co.edu.unicauca.academicprojectservice.Entity.Docente;
import co.edu.unicauca.academicprojectservice.Entity.EstadoProyecto;
import co.edu.unicauca.academicprojectservice.Repository.DocenteRepository;
import co.edu.unicauca.academicprojectservice.Repository.ProyectoRepository;
import co.edu.unicauca.academicprojectservice.infra.dto.DocenteDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DocenteService {

    @Autowired
    private DocenteRepository docenteRepository;
    @Autowired
    private ProyectoRepository proyectoRepository;

    public DocenteDTO obtenerDocentePorCorreo(String correo) {
        return docenteRepository.findByCorreo(correo)
                .map(d -> new DocenteDTO(
                        d.getNombres(),
                        d.getApellidos(),
                        d.getCelular(),
                        d.getCorreo(),
                        d.getDepartamento().name()
                ))
                .orElse(null);
    }

    public void agregarDocente(DocenteDTO dto) {
        Docente d = new Docente();
        d.setNombres(dto.getNombres());
        d.setApellidos(dto.getApellidos());
        d.setCelular(dto.getCelular());
        d.setCorreo(dto.getCorreo());
        d.setDepartamento(Departamento.valueOf(dto.getDepartamento().toUpperCase().replace(" ", "_")));
        docenteRepository.save(d);
    }

    public int countProyectosEnTramitePorCorreo(String correo) {
        return proyectoRepository.countByDocenteCorreoAndEstado(correo, EstadoProyecto.EN_TRAMITE);
    }
}

