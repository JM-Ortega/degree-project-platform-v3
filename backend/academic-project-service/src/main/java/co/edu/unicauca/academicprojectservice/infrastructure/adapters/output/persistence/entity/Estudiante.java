package co.edu.unicauca.academicprojectservice.infrastructure.adapters.output.persistence.entity;

import co.edu.unicauca.shared.contracts.model.Programa;
import jakarta.persistence.*;
import java.util.UUID;
import java.util.List;

@Entity
@Table(name = "estudiante")
public class Estudiante {
    @Id
    private UUID id;

    @Column(nullable = false)
    private String nombres;

    @Column(nullable = false)
    private String apellidos;

    private String celular;

    @Column(nullable = false)
    private String correo;

    @Enumerated(EnumType.STRING)
    private Programa programa;

    @ManyToMany(mappedBy = "estudiantes")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<Proyecto> trabajos;

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public Programa getPrograma() {
        return programa;
    }

    public void setPrograma(Programa programa) {
        this.programa = programa;
    }

    public List<Proyecto> getTrabajos() {
        return trabajos;
    }

    public void setTrabajos(List<Proyecto> trabajos) {
        this.trabajos = trabajos;
    }
}
