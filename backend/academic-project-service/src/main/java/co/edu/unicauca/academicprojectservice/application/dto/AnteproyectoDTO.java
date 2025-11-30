package co.edu.unicauca.academicprojectservice.application.dto;

import java.time.LocalDate;
import java.util.UUID;

public class AnteproyectoDTO {
    private UUID id;
    private String nombreArchivo;
    private String descripcion;
    private String titulo;
    private byte[] blob;
    private LocalDate fechaCreacion;
    private String estudianteNombre;
    private String estudianteCorreo;

    public AnteproyectoDTO() {
    }

    public AnteproyectoDTO(byte[] blob, String descripcion, String estudianteCorreo, String estudianteNombre, LocalDate fechaCreacion, UUID id, String nombreArchivo, String titulo) {
        this.blob = blob;
        this.descripcion = descripcion;
        this.estudianteCorreo = estudianteCorreo;
        this.estudianteNombre = estudianteNombre;
        this.fechaCreacion = fechaCreacion;
        this.id = id;
        this.nombreArchivo = nombreArchivo;
        this.titulo = titulo;
    }

    public String getNombreArchivo() {return nombreArchivo;}
    public void setNombreArchivo(String nombreArchivo) {this.nombreArchivo = nombreArchivo;}

    public String getTitulo() {return titulo;}
    public void setTitulo(String titulo) {this.titulo = titulo;}

    public String getDescripcion() {return descripcion;}
    public void setDescripcion(String descripcion) {this.descripcion = descripcion;}

    public byte[] getBlob() {return blob;}
    public void setBlob(byte[] blob) {this.blob = blob;}

    public LocalDate getFechaCreacion() {return fechaCreacion;}
    public void setFechaCreacion(LocalDate fechaCreacion) {this.fechaCreacion = fechaCreacion;}

    public UUID getId() {return id;}
    public void setId(UUID id) {this.id = id;}

    public String getEstudianteCorreo() {return estudianteCorreo;}
    public void setEstudianteCorreo(String estudianteCorreo) {this.estudianteCorreo = estudianteCorreo;}

    public String getEstudianteNombre() {return estudianteNombre;}
    public void setEstudianteNombre(String estudianteNombre) {this.estudianteNombre = estudianteNombre;}
}
