package co.edu.unicauca.academicprojectservice.domain.exceptions;

public class MaximoDeVersionesFormatoAException extends DomainException {
    public MaximoDeVersionesFormatoAException() {
        super("No se pueden enviar más de 3 versiones del FormatoA.");
    }
}