package co.edu.unicauca.academicprojectservice.adapter.out.persistence;

import co.edu.unicauca.academicprojectservice.adapter.out.persistence.repository.DocenteRepository;
import co.edu.unicauca.academicprojectservice.port.out.persistence.DbPortDocente;
import co.edu.unicauca.academicprojectservice.domain.model.DocenteId;

import co.edu.unicauca.shared.contracts.model.EstadoProyecto;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DbAdapterDocente implements DbPortDocente {

    private final DocenteRepository docenteRepository;


    /// --

    public DbAdapterDocente(DocenteRepository docenteRepository) {
        this.docenteRepository = docenteRepository;
    }

    @Override
    public Optional<DocenteId> findIdByCorreo(String correo) {
        return docenteRepository.findByCorreo(correo)
                .map(docente -> new DocenteId(docente.getId()));
    }


    public int countByDocenteCorreoAndEstadoNot(String correo, EstadoProyecto estadoProyecto) {
        return docenteRepository.countByDocenteCorreoAndEstadoNot(correo, estadoProyecto);
    }
}
