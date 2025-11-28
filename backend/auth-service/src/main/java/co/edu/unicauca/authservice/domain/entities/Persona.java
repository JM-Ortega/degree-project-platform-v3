package co.edu.unicauca.authservice.domain.entities;

import co.edu.unicauca.shared.contracts.model.Programa;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

import java.util.UUID;

/**
 * Entidad base abstracta que representa a una persona registrada en el sistema.
 *
 * <p>
 * Define la información común de identidad institucional (código, nombres,
 * apellidos), datos de contacto y la cuenta de usuario asociada.
 * </p>
 *
 * <p>
 * Es extendida por tipos concretos como {@link Estudiante}, {@link Docente},
 * {@link Coordinador} y {@link JefeDeDepartamento}.
 * </p>
 */
@Entity
@Table(name = "personas")
@Inheritance(strategy = InheritanceType.JOINED)
@Schema(description = "Entidad base que representa a una persona registrada en la universidad.")
public abstract class Persona {

    @Id
    @Column(length = 36)
    @Schema(
            description = "Identificador único (UUID) de la persona.",
            example = "f0b8c19a-21ac-4b3a-8e1b-8a3c256ca8cd"
    )
    private UUID id;

    @Column(nullable = false, unique = true, length = 30)
    @Schema(
            description = "Código institucional único de la persona.",
            example = "202312345"
    )
    private String codigo;

    @Column(nullable = false, length = 80)
    @Schema(
            description = "Nombres de la persona.",
            example = "Juan Sebastián"
    )
    private String nombres;

    @Column(nullable = false, length = 80)
    @Schema(
            description = "Apellidos de la persona.",
            example = "Ortega Narváez"
    )
    private String apellidos;

    @Column(length = 20)
    @Schema(
            description = "Número de celular de contacto.",
            example = "3145678901"
    )
    private String celular;

    @Enumerated(EnumType.STRING)
    @Column(length = 80)
    @Schema(
            description = "Programa académico al que pertenece la persona.",
            example = "INGENIERIA_DE_SISTEMAS"
    )
    private Programa programa;

    @OneToOne(cascade = CascadeType.ALL, optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    @Schema(description = "Cuenta de usuario asociada a la persona.")
    private Usuario usuario;

    protected Persona() {
        // Requerido por JPA
    }

    public Persona(UUID id,
                   String codigo,
                   String nombres,
                   String apellidos,
                   String celular,
                   Programa programa,
                   Usuario usuario) {
        this.id = id;
        this.codigo = codigo;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.celular = celular;
        this.programa = programa;
        this.usuario = usuario;
    }

    // Getters y setters
    public UUID getId() { return id; }
    public String getCodigo() { return codigo; }
    public String getNombres() { return nombres; }
    public String getApellidos() { return apellidos; }
    public String getCelular() { return celular; }
    public Programa getPrograma() { return programa; }
    public Usuario getUsuario() { return usuario; }

    public void setCodigo(String codigo) { this.codigo = codigo; }
    public void setNombres(String nombres) { this.nombres = nombres; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public void setCelular(String celular) { this.celular = celular; }
    public void setPrograma(Programa programa) { this.programa = programa; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
}
