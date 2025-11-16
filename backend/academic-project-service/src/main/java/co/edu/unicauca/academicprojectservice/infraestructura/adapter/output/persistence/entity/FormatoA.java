package co.edu.unicauca.academicprojectservice.infraestructura.adapter.output.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDate;


@Entity
@Table(name = "formatoA")
public class FormatoA {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int nroVersion;
    private String nombreFormato;

    @Column(name = "fecha_creacion")
    private LocalDate fechaCreacion;

    private byte[] blob;
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_archivo", nullable = false)
    private EstadoFormatoA estado;

    @ManyToOne
    @JoinColumn(name = "proyecto_id")
    @JsonIgnore
    private Proyecto proyecto;

    public FormatoA() {}

    public byte[] getBlob() {
        return blob;
    }

    public void setBlob(byte[] blob) {
        this.blob = blob;
    }

    public EstadoFormatoA getEstado() {
        return estado;
    }

    public void setEstado(EstadoFormatoA estado) {
        this.estado = estado;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDate fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreFormato() {
        return nombreFormato;
    }

    public void setNombreFormato(String nombreFormato) {
        this.nombreFormato = nombreFormato;
    }

    public Proyecto getProyecto() {
        return proyecto;
    }

    public void setProyecto(Proyecto proyecto) {
        this.proyecto = proyecto;
    }

    public int getNroVersion() {
        return nroVersion;
    }

    public void setNroVersion(int nroVersion) {
        this.nroVersion = nroVersion;
    }
}
