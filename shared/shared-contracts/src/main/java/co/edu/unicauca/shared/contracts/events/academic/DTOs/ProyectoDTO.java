package co.edu.unicauca.shared.contracts.events.academic.DTOs;

import co.edu.unicauca.shared.contracts.model.EstadoProyecto;
import co.edu.unicauca.shared.contracts.model.TipoProyecto;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class ProyectoDTO {
    private UUID id;
    private String titulo;
    private TipoProyecto tipoProyecto;
    private EstadoProyecto estado;
    private List<EstudianteDTO> estudiantes;
    private DocenteDTO director;
    private DocenteDTO codirector;
    private AnteproyectoDTO anteproyecto;
    private FormatoADTO formatoA;
    private byte[] cartaLaboral;
}