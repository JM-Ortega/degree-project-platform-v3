package co.edu.unicauca.departmentheadservice.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "docentes")
public class Docente {

    @Id
    private UUID personaId; // ID único de persona para el Docente

    private String nombre;
    private String email; // Correo electrónico del Docente

    // Constructor sin argumentos (requerido por JPA)
    protected Docente() {}

    // Constructor con parámetros
    public Docente(UUID personaId, String nombre, String email) {
        this.personaId = personaId;
        this.nombre = nombre;
        this.email = email;
    }

    // Getters y setters
    public UUID getPersonaId() {
        return personaId;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setPersonaId(UUID personaId) {
        this.personaId = personaId;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
