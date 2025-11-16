package co.edu.unicauca.academicprojectservice.domain.model;

import co.edu.unicauca.academicprojectservice.domain.exceptions.DomainException;
import java.util.UUID;

public class DocenteId {
    private final UUID value;

    public DocenteId(UUID value) {
        if (value == null) {
            throw new DomainException("El docente es obligatorio,");
        }
        this.value = value;
    }

    public UUID value() {
        return value;
    }
}
