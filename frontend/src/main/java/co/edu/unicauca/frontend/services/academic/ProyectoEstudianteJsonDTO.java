package co.edu.unicauca.frontend.services.academic;

import co.edu.unicauca.frontend.entities.EstadoProyecto;
import co.edu.unicauca.frontend.entities.TipoProyecto;

import java.util.UUID;

public class ProyectoEstudianteJsonDTO {
    private UUID id;
    private String titulo;
    private String nombreDirector;
    private TipoProyecto tipoProyecto;
    private EstadoProyecto estadoProyecto;

    public ProyectoEstudianteJsonDTO() {} // necesario para Jackson

    // getters y setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getNombreDirector() { return nombreDirector; }
    public void setNombreDirector(String nombreDirector) { this.nombreDirector = nombreDirector; }

    public TipoProyecto getTipoProyecto() { return tipoProyecto; }
    public void setTipoProyecto(TipoProyecto tipoProyecto) { this.tipoProyecto = tipoProyecto; }

    public EstadoProyecto getEstadoProyecto() { return estadoProyecto; }
    public void setEstadoProyecto(EstadoProyecto estadoProyecto) { this.estadoProyecto = estadoProyecto; }
}

