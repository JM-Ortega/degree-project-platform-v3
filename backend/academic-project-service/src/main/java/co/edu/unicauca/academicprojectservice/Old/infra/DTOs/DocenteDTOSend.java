package co.edu.unicauca.academicprojectservice.Old.infra.DTOs;

import co.edu.unicauca.shared.contracts.model.Departamento;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class DocenteDTOSend extends PersonaDTO {
    private Departamento departamento;
}
