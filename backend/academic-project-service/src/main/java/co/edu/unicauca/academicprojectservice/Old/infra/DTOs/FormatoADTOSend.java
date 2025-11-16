package co.edu.unicauca.academicprojectservice.Old.infra.DTOs;

import co.edu.unicauca.shared.contracts.model.EstadoFormatoA;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;


@Data
public class FormatoADTOSend {
    private UUID id;
    private UUID proyectoId;
    private int nroVersion;
    private String nombreFormatoA;
    private LocalDate fechaSubida;
    private byte[] blob;
    private EstadoFormatoA estado;
}