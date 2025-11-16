package co.edu.unicauca.academicprojectservice.infra.DTOs;

import co.edu.unicauca.shared.contracts.model.Departamento;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class DocenteDTOSend extends PersonaDTO {
    private Departamento departamento;
    @JsonBackReference
    private List<ProyectoDTOSend> trabajosComoDirector;
    @JsonBackReference
    private List<ProyectoDTOSend> trabajosComoCodirector;
}
