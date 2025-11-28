package co.edu.unicauca.authservice.domain.entities;

import co.edu.unicauca.shared.contracts.model.Programa;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

import java.util.UUID;

/**
 * Representa a un estudiante registrado en el sistema.
 *
 * <p>
 * Extiende la clase {@link Persona}, heredando atributos como:
 * código institucional, nombres, apellidos, celular,
 * programa académico y el usuario asociado para autenticación.
 * </p>
 *
 * <p>
 * A diferencia de otras entidades como {@link Docente} o
 * {@link JefeDeDepartamento}, un estudiante no define atributos
 * adicionales propios; únicamente especializa el rol dentro del dominio.
 * </p>
 */
@Entity
@Table(name = "estudiantes")
@Schema(description = "Entidad que representa a un estudiante de la universidad.")
public class Estudiante extends Persona {

    protected Estudiante() {
        // Constructor protegido requerido por JPA
    }

    public Estudiante(UUID id,
                      String codigo,
                      String nombres,
                      String apellidos,
                      String celular,
                      Programa programa,
                      Usuario usuario) {
        super(id, codigo, nombres, apellidos, celular, programa, usuario);
    }
}
