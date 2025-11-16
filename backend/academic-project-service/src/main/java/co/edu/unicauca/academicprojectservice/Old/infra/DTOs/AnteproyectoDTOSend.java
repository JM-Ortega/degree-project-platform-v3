package co.edu.unicauca.academicprojectservice.infra.DTOs;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Data
public class AnteproyectoDTOSend {
    private Long id;
    private String titulo;
    private String descripcion;
    private LocalDate fechaCreacion;
    private ProyectoDTOSend proyecto;
    private List<DocenteDTOSend> evaluadores;
}
