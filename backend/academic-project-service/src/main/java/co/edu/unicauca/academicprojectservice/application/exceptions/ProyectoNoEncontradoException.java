package co.edu.unicauca.academicprojectservice.application.exceptions;

import java.util.UUID;

public class ProyectoNoEncontradoException extends RuntimeException {
    public ProyectoNoEncontradoException(UUID id) {
        super("No se encontró el proyecto con id: " + id);
    }
}
