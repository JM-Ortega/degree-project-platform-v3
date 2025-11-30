package co.edu.unicauca.shared.contracts.events.academic.DTOs;

import co.edu.unicauca.shared.contracts.model.Departamento;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DocenteDTO extends PersonaDTO {
    private Departamento departamento;
}
