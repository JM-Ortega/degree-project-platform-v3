package co.edu.unicauca.academicprojectservice.port.in.rest;

import co.edu.unicauca.academicprojectservice.application.exceptions.ProyectoNoEncontradoException;
import co.edu.unicauca.academicprojectservice.domain.exceptions.DomainException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProyectoNoEncontradoException.class)
    public ResponseEntity<?> manejarProyectoNoEncontrado(ProyectoNoEncontradoException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<?> manejarDomainException(DomainException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> manejarExcepcionesGenerales(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error interno del servidor");
    }
}
