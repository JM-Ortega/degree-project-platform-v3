package co.edu.unicauca.authservice.domain.entities;

import co.edu.unicauca.shared.contracts.model.Programa;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

/**
 * Representa a un coordinador académico dentro de la universidad.
 *
 * <p>
 * Hereda de {@link Persona} y añade el atributo {@link #programaCoordinado},
 * que indica el programa académico que este usuario coordina.
 * A diferencia de {@link JefeDeDepartamento}, un coordinador se asocia
 * directamente a un <em>programa</em> específico y no a un departamento completo.
 * </p>
 */
@Entity
@Table(name = "coordinadores")
@Schema(description = "Entidad que representa a un coordinador de un programa académico.")
public class Coordinador extends Persona {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 100)
    @Schema(
            description = "Programa académico que coordina este usuario.",
            example = "INGENIERIA_DE_SISTEMAS"
    )
    private Programa programaCoordinado;

    protected Coordinador() {
        // Requerido por JPA
    }

    public Coordinador(String id,
                       String codigo,
                       String nombres,
                       String apellidos,
                       String celular,
                       Programa programa,
                       Usuario usuario,
                       Programa programaCoordinado) {
        super(id, codigo, nombres, apellidos, celular, programa, usuario);
        this.programaCoordinado = programaCoordinado;
    }

    public Programa getProgramaCoordinado() {
        return programaCoordinado;
    }

    public void setProgramaCoordinado(Programa programaCoordinado) {
        this.programaCoordinado = programaCoordinado;
    }
}
