package co.edu.unicauca.coordinatorservice.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "docente")
public class Docente {

    @Id
    @Column(columnDefinition = "uuid", nullable = false)
    private UUID id;  // Usamos el mismo UUID del servicio dueño

    private String nombres;
    private String apellidos;

    @Column(nullable = false, unique = true)
    private String email;

    private String celular;

    public Docente() {
        // Requerido por JPA
    }

    // === Getters & Setters ===
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
}
