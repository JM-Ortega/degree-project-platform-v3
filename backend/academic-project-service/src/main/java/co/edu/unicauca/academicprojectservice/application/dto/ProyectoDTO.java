package co.edu.unicauca.academicprojectservice.application.dto;

import co.edu.unicauca.shared.contracts.model.EstadoProyecto;
import co.edu.unicauca.shared.contracts.model.TipoProyecto;

import java.util.List;

public class ProyectoDTO {
    private String titulo;
    private List<String> estudiantes;
    private String director;
    private AnteproyectoDTO anteproyecto;
    private FormatoADTO formatoA;
    private CartaLaboralDTO cartaLaboral;
    private TipoProyecto tipoProyecto;
    private EstadoProyecto estadoProyecto;

    public AnteproyectoDTO getAnteproyecto() { return anteproyecto; }
    public void setAnteproyecto(AnteproyectoDTO anteproyecto) { this.anteproyecto = anteproyecto; }

    public String getDirector() { return director; }
    public void setDirector(String director) { this.director = director; }

    public CartaLaboralDTO getCartaLaboral() { return cartaLaboral; }
    public void setCartaLaboral(CartaLaboralDTO cartaLaboral) { this.cartaLaboral = cartaLaboral; }

    public EstadoProyecto getEstadoProyecto() { return estadoProyecto; }
    public void setEstadoProyecto(EstadoProyecto estadoProyecto) { this.estadoProyecto = estadoProyecto; }

    public List<String> getEstudiantes() { return estudiantes; }
    public void setEstudiantes(List<String> estudiantes) { this.estudiantes = estudiantes; }

    public FormatoADTO getFormatoA() { return formatoA; }
    public void setFormatoA(FormatoADTO formatoA) { this.formatoA = formatoA; }

    public TipoProyecto getTipoProyecto() { return tipoProyecto; }
    public void setTipoProyecto(TipoProyecto tipoProyecto) { this.tipoProyecto = tipoProyecto; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
}
