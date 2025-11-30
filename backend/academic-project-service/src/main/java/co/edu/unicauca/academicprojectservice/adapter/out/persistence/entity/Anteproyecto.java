package co.edu.unicauca.academicprojectservice.adapter.out.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "anteproyecto")
public class Anteproyecto {
    @Id
    private UUID id;

    private String nombreArchivo;
    private String descripcion;
    private String titulo;
    private byte[] blob;

    @Column(name = "fecha_creacion")
    private LocalDate fechaCreacion;

    @ManyToMany
    @JoinTable(
            name = "anteproyecto_evaluador",
            joinColumns = @JoinColumn(name = "anteproyecto_id"),
            inverseJoinColumns = @JoinColumn(name = "docente_id")
    )
    private List<Docente> evaluadores;

    public Anteproyecto() {}

    // Getters y setters
    public UUID getId() { return id; }
    public void setId(UUID id) {this.id = id;}

    public LocalDate getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDate fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public byte[] getBlob() { return blob; }
    public void setBlob(byte[] blob) { this.blob = blob; }

    public String getNombreArchivo() { return nombreArchivo; }
    public void setNombreArchivo(String nombreArchivo) { this.nombreArchivo = nombreArchivo; }

    public String getDescripcion() {return descripcion;}
    public void setDescripcion(String descripcion) {this.descripcion = descripcion;}

    public String getTitulo() {return titulo;}
    public void setTitulo(String titulo) {this.titulo = titulo;}

    public List<Docente> getEvaluadores() {return evaluadores;}
    public void setEvaluadores(List<Docente> evaluadores) {this.evaluadores = evaluadores;}
}
