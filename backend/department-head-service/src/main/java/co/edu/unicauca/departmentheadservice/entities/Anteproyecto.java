package co.edu.unicauca.departmentheadservice.entities;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "anteproyectos")
public class Anteproyecto {


    @Id
    @GeneratedValue
    @Column(name = "internal_id", columnDefinition = "uuid")
    private UUID id;


    @Column(name = "anteproyecto_id", columnDefinition = "uuid", unique = true, nullable = false)
    private UUID anteproyectoId;

    @Column(name = "proyecto_id", columnDefinition = "uuid", nullable = false)
    private UUID proyectoId;

    private String titulo;
    private String descripcion;
    private LocalDate fechaCreacion;
    private String estudianteCorreo;
    private String directorCorreo;
    private String departamento;

    @ManyToMany
    @JoinTable(
            name = "anteproyecto_docentes",
            joinColumns = @JoinColumn(name = "anteproyecto_internal_id"),
            inverseJoinColumns = @JoinColumn(name = "docente_id")
    )
    private List<Docente> evaluadores;

    protected Anteproyecto() {
    }

    public Anteproyecto(UUID anteproyectoId,
                        UUID proyectoId,
                        String titulo,
                        String descripcion,
                        LocalDate fechaCreacion,
                        List<Docente> evaluadores,
                        String estudianteCorreo,
                        String directorCorreo,
                        String departamento) {
        this.anteproyectoId = anteproyectoId;
        this.proyectoId = proyectoId;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fechaCreacion = fechaCreacion;
        this.evaluadores = evaluadores;
        this.estudianteCorreo = estudianteCorreo;
        this.directorCorreo = directorCorreo;
        this.departamento = departamento;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getAnteproyectoId() {
        return anteproyectoId;
    }

    public void setAnteproyectoId(UUID anteproyectoId) {
        this.anteproyectoId = anteproyectoId;
    }

    public UUID getProyectoId() {
        return proyectoId;
    }

    public void setProyectoId(UUID proyectoId) {
        this.proyectoId = proyectoId;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDate fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getEstudianteCorreo() {
        return estudianteCorreo;
    }

    public void setEstudianteCorreo(String estudianteCorreo) {
        this.estudianteCorreo = estudianteCorreo;
    }

    public String getDirectorCorreo() {
        return directorCorreo;
    }

    public void setDirectorCorreo(String directorCorreo) {
        this.directorCorreo = directorCorreo;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public List<Docente> getEvaluadores() {
        return evaluadores;
    }

    public void setEvaluadores(List<Docente> evaluadores) {
        this.evaluadores = evaluadores;
    }
}

