package co.edu.unicauca.academicprojectservice.application.dto;

import java.time.LocalDate;

public class CartaLaboralDTO {
    private String nombreCartaLaboral;
    private LocalDate fechaCreacion;
    private byte[] blob;

    public String getNombreCartaLaboral() {
        return nombreCartaLaboral;
    }

    public void setNombreCartaLaboral(String nombreCartaLaboral) {
        this.nombreCartaLaboral = nombreCartaLaboral;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDate fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public byte[] getBlob() {
        return blob;
    }

    public void setBlob(byte[] blob) {
        this.blob = blob;
    }
}
