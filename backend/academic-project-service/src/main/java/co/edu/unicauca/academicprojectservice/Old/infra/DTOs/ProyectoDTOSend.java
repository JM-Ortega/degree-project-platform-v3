package co.edu.unicauca.academicprojectservice.Old.infra.DTOs;

import co.edu.unicauca.shared.contracts.model.EstadoProyecto;
import co.edu.unicauca.shared.contracts.model.TipoProyecto;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class ProyectoDTOSend {
    private UUID id;
    private String titulo;
    private TipoProyecto tipoProyecto;
    private EstadoProyecto estado;
    private List<EstudianteDTOSend> estudiantes;
    private DocenteDTOSend director;
    private DocenteDTOSend codirector;
    private AnteproyectoDTOSend anteproyecto;
    private FormatoADTOSend formatoA;
    private byte[] cartaLaboral;
}