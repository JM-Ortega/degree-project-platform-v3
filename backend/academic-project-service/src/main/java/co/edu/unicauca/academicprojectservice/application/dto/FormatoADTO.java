package co.edu.unicauca.academicprojectservice.Old.infra.dto;

import co.edu.unicauca.shared.contracts.model.EstadoFormatoA;

import java.time.LocalDate;

public class FormatoADTO {
    private String nombreFormato;
    private byte[] blob;
    private int nroVersion;
    private LocalDate fechaCreacion;
    private EstadoFormatoA estado;

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
