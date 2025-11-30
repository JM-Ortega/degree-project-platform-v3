package co.edu.unicauca.frontend.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormatoAResumenDTO {
    private String id;
    private String nombreProyecto;
    private String nombreDirector;
    private String tipoProyecto;
    private LocalDate fechaSubida;
    private String estadoFormatoA;
    private int nroVersion ;
    private String nombreFormatoA;
}

