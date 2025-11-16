package co.edu.unicauca.academicprojectservice.domain.model;

import co.edu.unicauca.academicprojectservice.domain.exceptions.DomainException;
import co.edu.unicauca.shared.contracts.model.Departamento;

import java.util.UUID;

public class Docente {
    private UUID value;

    public Docente(UUID value) {
        if (value == null) {
            throw new DomainException("El docente es obligatorio,");
        }
        this.value = value;
    }

    public UUID value() {
        return 
    }
}
