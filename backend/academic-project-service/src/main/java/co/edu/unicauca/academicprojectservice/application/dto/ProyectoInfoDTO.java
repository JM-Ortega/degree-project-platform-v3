package co.edu.unicauca.academicprojectservice.application.dto;

import co.edu.unicauca.shared.contracts.model.EstadoProyecto;
import co.edu.unicauca.shared.contracts.model.TipoProyecto;
import java.util.UUID;

public class ProyectoInfoDTO {
    private UUID id;
    private String titulo;
    private TipoProyecto tipo;
    private EstadoProyecto estado;
    private String estudianteNombre;
    private String estudianteCorreo;

    public ProyectoInfoDTO(UUID id, String titulo, TipoProyecto tipo, EstadoProyecto estado,
                           String estudianteNombre, String estudianteCorreo) {
        this.id = id;
        this.titulo = titulo;
        this.tipo = tipo;
        this.estado = estado;
        this.estudianteNombre = estudianteNombre;
        this.estudianteCorreo = estudianteCorreo;
    }

    public ProyectoInfoDTO() {
    }

    public String getEstudianteCorreo() {return estudianteCorreo;}
    public void setEstudianteCorreo(String estudianteCorreo) {this.estudianteCorreo = estudianteCorreo;}

    public String getEstudianteNombre() {return estudianteNombre;}
    public void setEstudianteNombre(String estudianteNombre) {this.estudianteNombre = estudianteNombre;}

    public UUID getId() {return id;}
    public void setId(UUID id) {this.id = id;}

    public String getTitulo() {return titulo;}
    public void setTitulo(String titulo) {this.titulo = titulo;}

    public EstadoProyecto getEstado() {return estado;}
    public void setEstado(EstadoProyecto estado) {this.estado = estado;}

    public TipoProyecto getTipo() {return tipo;}
    public void setTipo(TipoProyecto tipo) {this.tipo = tipo;}
}