package co.edu.unicauca.frontend.infra.dto;

import java.time.LocalDate;
import java.util.Date;

public class CartaLaboralDTO {
    private String nombreCartaLaboral;
    private LocalDate fechaCreacion;
    private byte[] blob;

    public byte[] getBlob() {
        return blob;
    }

    public void setBlob(byte[] blob) {
        this.blob = blob;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDate fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getNombreCartaLaboral() {
        return nombreCartaLaboral;
    }

    public void setNombreCartaLaboral(String nombreCartaLaboral) {
        this.nombreCartaLaboral = nombreCartaLaboral;
    }

}
