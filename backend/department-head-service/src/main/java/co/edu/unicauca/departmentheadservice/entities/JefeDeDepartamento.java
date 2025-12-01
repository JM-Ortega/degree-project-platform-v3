package co.edu.unicauca.departmentheadservice.entities;

import co.edu.unicauca.shared.contracts.model.Departamento;
import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "jefe_departamento")
public class JefeDeDepartamento {

    @Id
    @Column(nullable = false, unique = true)
    private UUID personaId; // Guardar el ID de la persona (de UserCreatedEvent)

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private Departamento departamento;

    // Constructor sin argumentos (requerido por JPA)
    protected JefeDeDepartamento() {}

    // Constructor con parámetros
    public JefeDeDepartamento(UUID personaId, String nombre, String email, Departamento departamento) {
        this.personaId = personaId;
        this.nombre = nombre;
        this.email = email;
        this.departamento = departamento;
    }

    // Getters y setters
    public UUID getPersonaId() {
        return personaId;
    }

    public String getNombre() {
        return nombre;
    }

    public Departamento getDepartamento() {
        return departamento;
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

    public void setDepartamento(Departamento departamento) {
        this.departamento = departamento;
    }

    public  void setEmail(String email) {
        this.email = email;
    }
}
