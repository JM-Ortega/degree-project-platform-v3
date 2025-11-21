package co.edu.unicauca.academicprojectservice.Old.infra.DTOs;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class AnteproyectoDTOSend {
    private UUID id;
    private String titulo;
    private String descripcion;
    private LocalDate fechaCreacion;
    @JsonIgnore
    private ProyectoDTOSend proyecto;
    private List<DocenteDTOSend> evaluadores;
}
