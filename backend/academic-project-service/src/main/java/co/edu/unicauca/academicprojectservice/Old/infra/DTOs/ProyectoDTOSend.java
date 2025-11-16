package co.edu.unicauca.academicprojectservice.Old.infra.DTOs;

import co.edu.unicauca.shared.contracts.model.EstadoProyecto;
import co.edu.unicauca.shared.contracts.model.TipoProyecto;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;

import java.util.List;

@Data
public class ProyectoDTOSend {
    private Long id;
    private String titulo;
    private TipoProyecto tipoProyecto;
    private EstadoProyecto estado;
    @JsonManagedReference
    private List<EstudianteDTOSend> estudiantes;
    @JsonManagedReference
    private DocenteDTOSend director;
    @JsonManagedReference
    private DocenteDTOSend codirector;
    private AnteproyectoDTOSend anteproyecto;
    private FormatoADTOSend formatoA;
}