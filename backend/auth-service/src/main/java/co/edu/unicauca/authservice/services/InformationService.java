package co.edu.unicauca.authservice.services;

import co.edu.unicauca.authservice.access.CoorRepository;
import co.edu.unicauca.authservice.access.PersonaRepository;
import co.edu.unicauca.authservice.domain.entities.Coordinador;
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

    public String obtenerEmailCoordinadorPorPrograma (String programa){
        Coordinador coor = coorRepository.findByPrograma(Programa.valueOf(programa)).get();
        return coor.getUsuario().getEmail();
    }

    public List<String> obtenerTelefonosPorCorreo(List<String> correos) {
        return personaRepository.findCelularesByUsuarioEmailIn(correos);
    }
}
