package co.edu.unicauca.frontend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true) // ignora cualquier otro campo extra
public class AnteproyectoDto {

    @JsonProperty("anteproyectoId") // mapea "anteproyectoId" -> id
    private UUID id;

    private String titulo;
    private String descripcion;
    private String fechaCreacion;
    private List<String> evaluadores = new ArrayList<>();

    // Getters y Setters
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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(String fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public List<String> getEvaluadores() {
        return evaluadores;
    }

    public void setEvaluadores(List<String> evaluadores) {
        this.evaluadores = evaluadores;
    }
}
