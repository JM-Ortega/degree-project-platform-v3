package co.edu.unicauca.academicprojectservice.application.dto;

import java.util.UUID;

public class ProyectoEstudianteDTO {
    private UUID id;
    private String titulo;
    private String nombreDirector;
    private String tipoProyecto;
    private String estadoProyecto;

    public ProyectoEstudianteDTO(UUID id, String titulo, String nombreDirector, String tipoProyecto, String estadoProyecto) {
        this.id = id;
        this.titulo = titulo;
        this.nombreDirector = nombreDirector;
        this.tipoProyecto = tipoProyecto;
        this.estadoProyecto = estadoProyecto;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getNombreDirector() {
        return nombreDirector;
    }

    public void setNombreDirector(String nombreDirector) {
        this.nombreDirector = nombreDirector;
    }

    public String getTipoProyecto() {
        return tipoProyecto;
    }

    public void setTipoProyecto(String tipoProyecto) {
        this.tipoProyecto = tipoProyecto;
    }

    public String getEstadoProyecto() {
        return estadoProyecto;
    }

    public void setEstadoProyecto(String estadoProyecto) {
        this.estadoProyecto = estadoProyecto;
    }
}
