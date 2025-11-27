package co.edu.unicauca.shared.contracts.events.academic.DTOs;

import co.edu.unicauca.shared.contracts.model.Programa;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class EstudianteDTO extends PersonaDTO {
    private String codigo;
    private Programa programa;
    @JsonIgnore
    private List<ProyectoDTO> trabajos;
}