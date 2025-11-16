package co.edu.unicauca.academicprojectservice.domain.exceptions.formatoa;

import co.edu.unicauca.academicprojectservice.domain.exceptions.DomainException;

public class FormatoANoObservadoException extends DomainException {
    public FormatoANoObservadoException() {
        super("El FormatoA solo puede actualizarse si está OBSERVADO.");
    }
}