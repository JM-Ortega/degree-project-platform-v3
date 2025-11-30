package co.edu.unicauca.shared.contracts.events.academic.DTOs;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class AnteproyectoDTO {
    private UUID id;
    private String titulo;
    private String descripcion;
    private LocalDate fechaCreacion;
    @JsonIgnore
    private ProyectoDTO proyecto;
    private List<DocenteDTO> evaluadores;
}
