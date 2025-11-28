package co.edu.unicauca.authservice.domain.entities;

import co.edu.unicauca.shared.contracts.model.Programa;
import co.edu.unicauca.shared.contracts.model.Departamento;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

import java.util.UUID;

/**
 * Representa al jefe de un departamento académico dentro de la universidad.
 *
 * <p>
 * Extiende la clase {@link Persona}, heredando atributos como nombres,
 * apellidos, celular, programa académico de origen y el usuario asociado
 * para autenticación.
 * </p>
 *
 * <p>
 * A diferencia de {@link Docente} o {@link Estudiante}, esta entidad define
 * un atributo adicional: el {@link Departamento} que el usuario dirige.
 * Este departamento representa una unidad administrativa completa dentro
 * de la Facultad.
 * </p>
 */
@Entity
@Table(name = "jefes_departamento")
@Schema(description = "Entidad que representa al jefe de un departamento académico.")
public class JefeDeDepartamento extends Persona {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 80)
    @Schema(
            description = "Departamento a cargo del jefe de departamento.",
            example = "TELEMATICA"
    )
    private Departamento departamento;

    protected JefeDeDepartamento() {
        // Constructor requerido por JPA
    }

    public JefeDeDepartamento(UUID id,
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
