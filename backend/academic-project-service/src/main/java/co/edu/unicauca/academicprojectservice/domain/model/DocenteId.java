package co.edu.unicauca.academicprojectservice.domain.model;

import co.edu.unicauca.academicprojectservice.domain.exceptions.DomainException;
import java.util.UUID;

public final class DocenteId {
    private final UUID value;

    public DocenteId(UUID value) {
        if (value == null) {
            throw new DomainException("El id del docente es obligatorio.");
        }
        this.value = value;
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DocenteId)) return false;
        DocenteId that = (DocenteId) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value.toString();
    }
}

