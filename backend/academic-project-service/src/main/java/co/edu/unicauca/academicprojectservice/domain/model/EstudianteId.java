package co.edu.unicauca.academicprojectservice.domain.model;

import co.edu.unicauca.academicprojectservice.domain.exceptions.DomainException;

import java.util.UUID;

public class EstudianteId {

    private final UUID value;

    public EstudianteId(UUID value) {
        if (value == null) throw new DomainException("El id del estudiante es obligatorio.");
        this.value = value;
    }

    public UUID value() {
        return value;
    }
}