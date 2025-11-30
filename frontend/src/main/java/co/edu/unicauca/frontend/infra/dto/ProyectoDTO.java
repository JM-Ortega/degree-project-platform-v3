package co.edu.unicauca.frontend.infra.dto;

import co.edu.unicauca.frontend.entities.EstadoProyecto;
import co.edu.unicauca.frontend.entities.TipoProyecto;

import java.util.List;
import java.util.UUID;

public class ProyectoDTO {
    private UUID id;
    private String titulo;
    private List<String> estudiantes;
    private String director;
    private AnteproyectoDTO anteproyectoDTO;
    private FormatoADTO formatoADTO;
    private CartaLaboralDTO cartaLaboralDTO;
    private TipoProyecto tipoProyecto;
    private EstadoProyecto estadoProyecto;

    public UUID getId() {return id;}
    public void setId(UUID id) {this.id = id;}

    public AnteproyectoDTO getAnteproyecto() {
        return anteproyectoDTO;
    }
    public void setAnteproyecto(AnteproyectoDTO anteproyectoDTO) {
        this.anteproyectoDTO = anteproyectoDTO;
    }

    public CartaLaboralDTO getCartaLaboral() {
        return cartaLaboralDTO;
    }
    public void setCartaLaboral(CartaLaboralDTO cartaLaboralDTO) {
        this.cartaLaboralDTO = cartaLaboralDTO;
    }

    public String getDirector() {
        return director;
    }
    public void setDirector(String director) {
        this.director = director;
    }

    public EstadoProyecto getEstadoProyecto() {
        return estadoProyecto;
    }
    public void setEstadoProyecto(EstadoProyecto estadoProyecto) {
        this.estadoProyecto = estadoProyecto;
    }

    public List<String> getEstudiantes() {
        return estudiantes;
    }
    public void setEstudiantes(List<String> estudiante) {
        this.estudiantes = estudiante;
    }

    public FormatoADTO getFormatoA() {
        return formatoADTO;
    }
    public void setFormatoA(FormatoADTO formatoADTO) {
        this.formatoADTO = formatoADTO;
    }

    public TipoProyecto getTipoProyecto() {
        return tipoProyecto;
    }
    public void setTipoProyecto(TipoProyecto tipoProyecto) {
        this.tipoProyecto = tipoProyecto;
    }

    public String getTitulo() {
        return titulo;
    }
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
}
