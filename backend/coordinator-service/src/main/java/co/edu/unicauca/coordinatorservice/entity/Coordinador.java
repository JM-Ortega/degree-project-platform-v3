package co.edu.unicauca.coordinatorservice.entity;

import co.edu.unicauca.shared.contracts.model.Programa;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "coordinador")
public class Coordinador implements Serializable {

    @Id
    @Column(columnDefinition = "uuid", nullable = false)
    private UUID id;  // Mismo UUID que viene del auth-service

    private String nombres;

    @Column(nullable = false, unique = true)
    private String correo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Programa programa;

    public Coordinador() {
        // Constructor requerido por JPA
    }

    // === Getters & Setters ===
    public UUID getId() { return id; }

    public void setId(UUID id) { this.id = id; }

    public String getNombres() { return nombres; }

    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getCorreo() { return correo; }

    public void setCorreo(String correo) { this.correo = correo; }

    public Programa getPrograma() { return programa; }

    public void setPrograma(Programa programa) { this.programa = programa; }
}
