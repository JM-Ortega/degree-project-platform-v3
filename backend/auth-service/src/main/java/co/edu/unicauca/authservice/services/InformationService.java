package co.edu.unicauca.authservice.services;

import co.edu.unicauca.authservice.access.CoorRepository;
import co.edu.unicauca.authservice.access.JefeRepository;
import co.edu.unicauca.authservice.access.PersonaRepository;
import co.edu.unicauca.authservice.domain.entities.Coordinador;
import co.edu.unicauca.authservice.domain.entities.JefeDeDepartamento;
import co.edu.unicauca.shared.contracts.model.Departamento;
import co.edu.unicauca.shared.contracts.model.Programa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InformationService {
    @Autowired
    private CoorRepository coorRepository;

    @Autowired
    private PersonaRepository personaRepository;

    @Autowired
    private JefeRepository jefeRepository;

    public String obtenerEmailCoordinadorPorPrograma (String programa){
        Coordinador coor = coorRepository.findByPrograma(Programa.valueOf(programa)).get();
        return coor.getUsuario().getEmail();
    }

    public String obtenerEmailJefePorDepartamento (String departamento){
        JefeDeDepartamento jefe = jefeRepository.findByDepartamento(Departamento.valueOf(departamento)).get();
        return jefe.getUsuario().getEmail();
    }

    public String obtenerTelefonoPorCorreo(String correo) {
        return personaRepository.findCelularByEmail(correo);
    }
}
