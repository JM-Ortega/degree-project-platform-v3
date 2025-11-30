package co.edu.unicauca.authservice.domain.entities;

import co.edu.unicauca.shared.contracts.model.Departamento;
import co.edu.unicauca.shared.contracts.model.Programa;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

import java.util.UUID;

/**
 * Representa a un docente registrado en el sistema.
 *
 * <p>
 * Extiende la clase {@link Persona}, heredando atributos como
 * nombres, apellidos, celular, programa académico y el usuario
 * asociado para autenticación.
 * </p>
 *
 * <p>
 * Además, un docente está vinculado a un {@link Departamento},
 * el cual define la unidad académica a la que pertenece dentro
 * de la Facultad.
 * </p>
 */
@Entity
@Table(name = "docentes")
@Schema(description = "Entidad que representa a un docente de la Facultad.")
public class Docente extends Persona {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 80)
    @Schema(
            description = "Departamento académico al que pertenece el docente.",
            example = "SISTEMAS"
    )
    private Departamento departamento;

    protected Docente() {
        // Constructor protegido requerido por JPA
    }

    public Docente(UUID id,
                   String codigo,
                   String nombres,
                   String apellidos,
                   String celular,
                   Programa programa,
                   Usuario usuario,
                   Departamento departamento) {
        super(id, codigo, nombres, apellidos, celular, programa, usuario);
        this.departamento = departamento;
    }

    public Departamento getDepartamento() {
        return departamento;
    }

    public void setDepartamento(Departamento departamento) {
        this.departamento = departamento;
    }
}
