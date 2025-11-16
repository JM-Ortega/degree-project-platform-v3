package co.edu.unicauca.academicprojectservice.infraestructura.adapter.output.persistence.entity;

import co.edu.unicauca.shared.contracts.model.Departamento;
import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(
    name = "docente",
    indexes = {
        @Index(name = "idx_docente_correo", columnList = "correo")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_docente_correo", columnNames = "correo")
    }
)
public class Docente {
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
    @Column(length = 60)
    private Departamento departamento;

    @OneToMany(mappedBy = "director")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<Proyecto> trabajosComoDirector;

    @OneToMany(mappedBy = "codirector")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<Proyecto> trabajosComoCodirector;

    @ManyToMany(mappedBy = "evaluadores")
    private List<Anteproyecto> anteproyectosAEvaluar;

    public Docente() {}

    // Getters/Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getCelular() { return celular; }
    public void setCelular(String celular) { this.celular = celular; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) {
        this.correo = (correo == null) ? null : correo.trim().toLowerCase();
    }

    public Departamento getDepartamento() { return departamento; }
    public void setDepartamento(Departamento departamento) { this.departamento = departamento; }

    public List<Proyecto> getTrabajosComoDirector() { return trabajosComoDirector; }
    public void setTrabajosComoDirector(List<Proyecto> trabajosComoDirector) { this.trabajosComoDirector = trabajosComoDirector; }

    public List<Proyecto> getTrabajosComoCodirector() { return trabajosComoCodirector; }
    public void setTrabajosComoCodirector(List<Proyecto> trabajosComoCodirector) { this.trabajosComoCodirector = trabajosComoCodirector; }

    public List<Anteproyecto> getAnteproyectosAEvaluar() { return anteproyectosAEvaluar; }
    public void setAnteproyectosAEvaluar(List<Anteproyecto> anteproyectosAEvaluar) { this.anteproyectosAEvaluar = anteproyectosAEvaluar; }
}
