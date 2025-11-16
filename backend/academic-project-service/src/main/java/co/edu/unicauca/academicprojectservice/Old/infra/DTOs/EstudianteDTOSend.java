package co.edu.unicauca.academicprojectservice.infra.DTOs;

import co.edu.unicauca.shared.contracts.model.Programa;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class EstudianteDTOSend extends PersonaDTO {
    private String codigo;
    private Programa programa;
    @JsonBackReference
    private List<ProyectoDTOSend> trabajos;
}