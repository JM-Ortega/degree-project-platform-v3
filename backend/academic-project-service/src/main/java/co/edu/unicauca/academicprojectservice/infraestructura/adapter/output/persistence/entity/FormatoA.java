package co.edu.unicauca.academicprojectservice.infraestructura.adapter.output.persistence.entity;

import co.edu.unicauca.shared.contracts.model.EstadoFormatoA;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;


@Entity
@Table(name = "formatoA")
public class FormatoA {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private int nroVersion;
    private String nombreFormato;

    @Column(name = "fecha_creacion")
    private LocalDate fechaCreacion;

    private byte[] blob;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_archivo", nullable = false)
    private EstadoFormatoA estado;

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

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNombreFormato() {
        return nombreFormato;
    }

    public void setNombreFormato(String nombreFormato) {
        this.nombreFormato = nombreFormato;
    }

    public int getNroVersion() {
        return nroVersion;
    }

    public void setNroVersion(int nroVersion) {
        this.nroVersion = nroVersion;
    }
}
