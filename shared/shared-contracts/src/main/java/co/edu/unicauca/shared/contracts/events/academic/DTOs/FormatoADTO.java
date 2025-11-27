package co.edu.unicauca.shared.contracts.events.academic.DTOs;

import co.edu.unicauca.shared.contracts.model.EstadoFormatoA;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;


@Data
public class FormatoADTO {
    private UUID id;
    private UUID proyectoId;
    private int nroVersion;
    private String nombreFormatoA;
    private LocalDate fechaSubida;
    private byte[] blob;
    private EstadoFormatoA estado;
}