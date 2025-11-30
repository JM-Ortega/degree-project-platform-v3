package co.edu.unicauca.coordinatorservice.entity;

import co.edu.unicauca.shared.contracts.model.Programa;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "estudiante")
public class Estudiante {

    @Id
    @Column(columnDefinition = "uuid", nullable = false)
    private UUID id;  // mismo UUID que en el micro dueño (auth)

    // Datos personales básicos (PersonaDTO)
    private String nombres;
    private String apellidos;

    @Column(nullable = false, unique = true)
    private String email;

    private String celular;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Programa programa;

    // Opcional, si lo usas en EstudianteDTO
    private String codigo;

    public Estudiante() {
        // Requerido por JPA
    }

    // ===== Getters & Setters =====
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCelular() { return celular; }
    public void setCelular(String celular) { this.celular = celular; }

    public Programa getPrograma() { return programa; }
    public void setPrograma(Programa programa) { this.programa = programa; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
}
