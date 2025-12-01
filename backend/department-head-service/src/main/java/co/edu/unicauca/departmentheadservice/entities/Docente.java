package co.edu.unicauca.departmentheadservice.entities;


import co.edu.unicauca.shared.contracts.model.Departamento;
import co.edu.unicauca.shared.contracts.model.Rol;
import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "docentes")
public class Docente {

    @Id
    private UUID personaId; // ID único de persona para el Docente

    private String nombre;
    private String email; // Correo electrónico del Docente

    private Departamento departamento;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private List<Rol> roles;

    // Constructor sin argumentos (requerido por JPA)
    protected Docente() {}

    // Constructor con parámetros
    public Docente(UUID personaId, String nombre, String email, Departamento departamento, List<Rol> roles) {
        this.personaId = personaId;
        this.nombre = nombre;
        this.email = email;
        this.departamento = departamento;
        this.roles = roles;
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

    public Departamento getDepartamento() {
        return departamento;
    }

    public void setDepartamento(Departamento departamento) {
        this.departamento = departamento;
    }

    public List<Rol> getRoles() {
        return roles;
    }

    public void setRoles(List<Rol> rol) {
        this.roles = rol;
    }
}
