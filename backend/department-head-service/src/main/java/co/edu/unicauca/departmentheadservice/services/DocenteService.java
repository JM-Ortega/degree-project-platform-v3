package co.edu.unicauca.departmentheadservice.services;

import co.edu.unicauca.departmentheadservice.access.DocenteRepository;
import co.edu.unicauca.departmentheadservice.entities.Docente;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocenteService {
    private final DocenteRepository docenteRepository;

    public DocenteService(DocenteRepository docenteRepository) {
        this.docenteRepository = docenteRepository;
    }

    public List<Docente> obtenerDocentes(String correoJefe) {
        return docenteRepository.findAllDocentesByEmailDepartamento(correoJefe);
    }
}
