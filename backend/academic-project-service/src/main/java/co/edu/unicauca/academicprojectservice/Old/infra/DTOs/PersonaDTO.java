package co.edu.unicauca.academicprojectservice.Old.infra.DTOs;

import co.edu.unicauca.shared.contracts.model.Programa;
import co.edu.unicauca.shared.contracts.model.Rol;

import lombok.Data;

import java.util.UUID;

@Data
public class PersonaDTO {
    private UUID id;
    private String nombres;
    private String apellidos;
    private String celular;
    private Programa programa;
    private String email;
    private String passwordHash;
    private Rol rol;
}
