package co.edu.unicauca.academicprojectservice.port.in.rest;

import co.edu.unicauca.academicprojectservice.application.exceptions.ProyectoNoEncontradoException;
import co.edu.unicauca.academicprojectservice.domain.exceptions.DomainException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ProyectoNoEncontradoException.class)
    public ResponseEntity<?> manejarProyectoNoEncontrado(ProyectoNoEncontradoException ex) {
        log.warn("Proyecto no encontrado", ex);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<?> manejarDomainException(DomainException ex) {
        log.warn("Error de dominio", ex);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> manejarExcepcionesGenerales(Exception ex) {
        log.error("Error interno no controlado", ex);  // <--- IMPORTANTE
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error interno del servidor");
    }
}
