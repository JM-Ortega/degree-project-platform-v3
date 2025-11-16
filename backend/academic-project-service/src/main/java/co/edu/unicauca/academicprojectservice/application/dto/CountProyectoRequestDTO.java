package co.edu.unicauca.academicprojectservice.Old.infra.dto;

import co.edu.unicauca.shared.contracts.model.EstadoProyecto;
import co.edu.unicauca.shared.contracts.model.TipoProyecto;

public class CountProyectoRequestDTO {
    private TipoProyecto tipoProyecto;
    private EstadoProyecto estadoProyecto;
    private String correoDocente;

    public String getCorreoDocente() {return correoDocente;}
    public void setCorreoDocente(String correoDocente) {this.correoDocente = correoDocente;}

    public EstadoProyecto getEstadoProyecto() {return estadoProyecto;}
    public void setEstadoProyecto(EstadoProyecto estadoProyecto) {this.estadoProyecto = estadoProyecto;}

    public TipoProyecto getTipoProyecto() {return tipoProyecto;}
    public void setTipoProyecto(TipoProyecto tipoProyecto) {this.tipoProyecto = tipoProyecto;}
}
