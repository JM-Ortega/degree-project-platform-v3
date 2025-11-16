package co.edu.unicauca.academicprojectservice.domain.exceptions;

public class FormatoANoObservadoException extends DomainException {
    public FormatoANoObservadoException() {
        super("El FormatoA solo puede actualizarse si está OBSERVADO.");
    }
}