package co.edu.unicauca.frontend.infra.dto;

import co.edu.unicauca.frontend.entities.EstadoProyecto;
import co.edu.unicauca.frontend.entities.TipoProyecto;

import java.util.UUID;

public class ProyectoEstudianteDTO {
    private UUID id;
    private String titulo;
    private String nombreDirector;
    private TipoProyecto tipoProyecto;
    private EstadoProyecto estadoProyecto;

    public ProyectoEstudianteDTO(UUID id, String titulo, String nombreDirector,
                                 TipoProyecto tipoProyecto, EstadoProyecto estadoProyecto) {
        this.id = id;
        this.titulo = titulo;
        this.nombreDirector = nombreDirector;
        this.tipoProyecto = tipoProyecto;
        this.estadoProyecto = estadoProyecto;
    }

    public UUID getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getNombreDirector() { return nombreDirector; }
    public TipoProyecto getTipoProyecto() { return tipoProyecto; }
    public EstadoProyecto getEstadoProyecto() { return estadoProyecto; }
}
