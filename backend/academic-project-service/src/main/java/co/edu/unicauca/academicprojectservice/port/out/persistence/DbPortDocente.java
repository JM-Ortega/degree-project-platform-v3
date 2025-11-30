package co.edu.unicauca.academicprojectservice.port.out.persistence;


import co.edu.unicauca.academicprojectservice.domain.model.DocenteId;
import co.edu.unicauca.shared.contracts.model.EstadoProyecto;

import java.util.Optional;

public interface DbPortDocente {
    // nuevo
    Optional<DocenteId> findIdByCorreo(String correo);



    //...


    int countByDocenteCorreoAndEstadoNot(String correo, EstadoProyecto estadoProyecto);
}
