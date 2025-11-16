package co.edu.unicauca.academicprojectservice.infraestructura.adapter.output.persistence.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "anteproyecto")
public class Anteproyecto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
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

    @OneToOne(mappedBy = "anteproyecto")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Proyecto proyecto;

    public Anteproyecto() {}

    // Getters y setters
    public Long getId() { return id; }

    public LocalDate getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDate fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public Proyecto getProyecto() { return proyecto; }
    public void setProyecto(Proyecto proyecto) { this.proyecto = proyecto; }

    public byte[] getBlob() { return blob; }
    public void setBlob(byte[] blob) { this.blob = blob; }

    public String getNombreArchivo() { return nombreArchivo; }
    public void setNombreArchivo(String nombreArchivo) { this.nombreArchivo = nombreArchivo; }

    public String getDescripcion() {return descripcion;}
    public void setDescripcion(String descripcion) {this.descripcion = descripcion;}

    public void setId(Long id) {this.id = id;}
    public String getTitulo() {return titulo;}
    public void setTitulo(String titulo) {this.titulo = titulo;}

    public List<Docente> getEvaluadores() {return evaluadores;}
    public void setEvaluadores(List<Docente> evaluadores) {this.evaluadores = evaluadores;}
}
