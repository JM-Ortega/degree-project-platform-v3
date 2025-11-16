package co.edu.unicauca.academicprojectservice.application.dto;

import co.edu.unicauca.academicprojectservice.domain.model.Anteproyecto;
import co.edu.unicauca.academicprojectservice.domain.model.FormatoA;
import co.edu.unicauca.shared.contracts.model.EstadoProyecto;
import co.edu.unicauca.shared.contracts.model.TipoProyecto;


public class ProyectoDTO {
    private String titulo;
    private String estudiante;
    private String director;
    private Anteproyecto anteproyecto;
    private FormatoA formatoA;
    private byte[] cartaLaboral;
    private TipoProyecto tipoProyecto;
    private EstadoProyecto estadoProyecto;

    public Anteproyecto getAnteproyecto() {return anteproyecto;}
    public void setAnteproyecto(Anteproyecto anteproyecto) {this.anteproyecto = anteproyecto;}

    public String getDirector() {return director;}
    public void setDirector(String director) {this.director = director;}

    public byte[] getCartaLaboral() {return cartaLaboral;}
    public void setCartaLaboral(byte[] cartaLaboral) {this.cartaLaboral = cartaLaboral;}

    public EstadoProyecto getEstadoProyecto() {return estadoProyecto;}
    public void setEstadoProyecto(EstadoProyecto estadoProyecto) {this.estadoProyecto = estadoProyecto;}

    public String getEstudiante() {return estudiante;}
    public void setEstudiante(String estudiante) {this.estudiante = estudiante;}

    public FormatoA getFormatoA() {return formatoA;}
    public void setFormatoA(FormatoA formatoA) {this.formatoA = formatoA;}

    public TipoProyecto getTipoProyecto() {return tipoProyecto;}
    public void setTipoProyecto(TipoProyecto tipoProyecto) {this.tipoProyecto = tipoProyecto;}

    public String getTitulo() {return titulo;}
    public void setTitulo(String titulo) {this.titulo = titulo;}
}
